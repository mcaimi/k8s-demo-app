#!/usr/bin/env python3
#
# Simple fetcher for artifacts stored into a Nexus Repository
#
# v0.1 - DevOps Workshop 
#

from hashlib import sha1, md5
from argparse import ArgumentParser, SUPPRESS
import base64
import os,re,sys
from urllib import request, error
from json import loads

class MalformedArgumentException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

class MissingArgumentException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

class InvalidRepoException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

class InvalidDeployPathException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

class FetchError(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

class FileCorruptedException(Exception):
    def __init__(self, *args, **kwargs):
        super().__init__(self, *args, **kwargs)

ALLOWED_PROTOS = [ "http", "https" ]
SUPPORTED_FORMATS = [ "jar", "war" ]
NEXUS_REST_PATH = "service/rest/v1/search/assets"

# Parsed options wrapper
class Wrapper():
    def __init__(self, hash_info):
        if not (hash_info.__class__ == dict):
            raise MalformedArgumentException("Parameter class is not Hash, got [%s]" % hash_info.__class__)
        
        self._wrap(hash_info)

    def _wrap(self, infos):
        for key in infos.keys():
            element = infos.get(key)
            if element.__class__ == dict:
                setattr(self, key, Wrapper(element))
            elif element.__class__ == list:
                setattr(self, key, [])
                embedded_list = getattr(self, key)
                for item in element:
                    embedded_list.append(Wrapper(item))
            else:
                setattr(self, key, element)

# Command Line Options Parser
class Parser():
    def __init__(self):
        self.parser = ArgumentParser(argument_default=SUPPRESS)
        self.parser.add_argument('-r', '--repo_url', dest='repo_url', default="nexus.apps.kubernetes.local", help="Default is nexus.apps.kubernetes.local")
        self.parser.add_argument('-a', '--artifact', dest='artifact_raw', help="The artifact that has to be retrieved from nexus. Format is groupID:artifactID:version.")
        self.parser.add_argument('-d', '--deploy_dir', dest ='deploy_dir', default='/var/lib/deploy')
        self.parser.add_argument('-p', '--protocol', dest='repo_proto', default="http")
        self.parser.add_argument('-f', '--format', dest='artifact_format', default="jar")
        self.parser.add_argument('-c', '--credentials', dest='nexus_credentials', default="admin:admin", help="Credentials used to authenticate on Nexus. Format is username:password")

        self.parsed_arguments = self.parser.parse_args()

    def _validate_format(self):
        if (self.parsed_arguments.artifact_format in SUPPORTED_FORMATS):
            setattr(self, 'artifact_format', self.parsed_arguments.artifact_format)
        else:
            raise MalformedArgumentException("Unsupported Artifact Format")

    def _validate_dir(self):
        if not os.path.isdir(self.parsed_arguments.deploy_dir):
            raise InvalidDeployPathException("Deploy target path is invalid.")

        setattr(self, 'deploy_dir', self.parsed_arguments.deploy_dir)

    def _validate_url(self):
        proto_is_valid = (self.parsed_arguments.repo_proto in ALLOWED_PROTOS)
        host_validator = re.compile(r"([a-z\d-]{1,63})+", re.IGNORECASE)
        hostname_is_valid = all([ host_validator.match(component) for component in self.parsed_arguments.repo_url.split(".") ])

        if proto_is_valid and hostname_is_valid:
            setattr(self, 'repo_proto', self.parsed_arguments.repo_proto)
            setattr(self, 'repo_url', self.parsed_arguments.repo_url)
        else:
            raise InvalidRepoException("Unsupported Protocol or Invalid Nexus Repo Hostname.")

    def _validate_credentials(self):
        creds = self.parsed_arguments.nexus_credentials.split(":")
        if len(creds) < 2:
            raise MalformedArgumentException("Malformed value for credentials argument")
        else:
            setattr(self, 'username', creds[0])
            setattr(self, 'password', creds[1])

    def _validate_artifact(self):
        artifact_components = self.parsed_arguments.artifact_raw.split(":")
        if len(artifact_components) < 3:
            raise MalformedArgumentException("Malformed Value.")
        else:
          setattr(self, 'groupID', artifact_components[0])
          setattr(self, 'artifactID', artifact_components[1])
          setattr(self, 'artifactVersion', artifact_components[2])

    def parse(self):
        try:
            self._validate_url()
        except InvalidRepoException as invalid_repo:
            raise InvalidRepoException(invalid_repo.__str__())

        try:
            self._validate_credentials()
            self._validate_format()
        except MalformedArgumentException as malformed_credentials:
            raise MalformedArgumentException(malformed_credentials.__str__())

        try:
            self._validate_dir()
        except InvalidDeployPathException as invalid_path:
            raise InvalidDeployPathException(invalid_path.__str__())

        if hasattr(self.parsed_arguments, 'artifact_raw'):
            try: 
                self._validate_artifact()
            except MalformedArgumentException as malformed_exception:
                raise MalformedArgumentException(malformed_exception.__str__())
        else:
            raise MissingArgumentException("Missing value for command line parameter '--artifact'.")

        return Wrapper({ 'repo_url': self.repo_url, 
            'repo_proto': self.repo_proto, 
            'deploy_dir': self.deploy_dir, 
            'username': self.username,
            'password': self.password,
            'artifact': {
                'groupID': self.groupID,
                'ID': self.artifactID,
                'version': self.artifactVersion,
                'format': self.artifact_format
                }
            })

# Nexus Artifact Downloader
class NexusDownloader():
    def __init__(self, parameters):
        self.parm_hash = parameters
        self.base_url = "%s://%s" % (self.parm_hash.repo_proto, self.parm_hash.repo_url)
        self.base_search_url = "%s/%s?sort=version" % (self.base_url, NEXUS_REST_PATH)
        self.base_download_url = "%s/%s/download?sort=version" % (self.base_url, NEXUS_REST_PATH)
        self.md5computer = md5()
        self.sha1computer = sha1()
        self.HASH_BUFFER_SIZE = 64*1024

    def build_search_parameters(self):
        print("Using base_url [%s]..." % self.base_search_url)
        self.url_parameters = "&group=%s&name=%s&maven.extension=%s&maven.classifier" % (self.parm_hash.artifact.groupID,
                                                                                        self.parm_hash.artifact.ID,
                                                                                        self.parm_hash.artifact.format)
    
    def _compute_hashes(self, filename):
        with open(filename, 'rb') as descriptor:
            while True:
                chunk = descriptor.read(self.HASH_BUFFER_SIZE)
                if not chunk:
                    break
                self.md5computer.update(chunk)
                self.sha1computer.update(chunk)

        return ("{0}".format(self.md5computer.hexdigest()), "{0}".format(self.sha1computer.hexdigest()))

    def pull_artifact(self):
        url_to_get = "%s%s" % (self.base_search_url, self.url_parameters)
        print("Trying to contact [%s]..." % url_to_get)

        auth_hash = base64.b64encode(bytes("%s:%s" % (self.parm_hash.username, self.parm_hash.password), 'ascii'))

        http_request_object = request.Request(url_to_get)
        http_request_object.add_header('Authorization', 'Basic %s' % auth_hash.decode())
        try: 
            self.search_results = request.urlopen(http_request_object)

            if self.search_results.code == 200:
                print(self.search_results.info())
                content = Wrapper(loads(self.search_results.read().decode()))

                if not len(content.items) > 0:
                    raise FetchError("Artifact Not Found") 

                downloadUrl = content.items[0].downloadUrl
                shaDigest = content.items[0].checksum.sha1
                md5Digest = content.items[0].checksum.md5

                print ("Trying to download %s..." % downloadUrl)

                download_request = request.Request(downloadUrl)
                download_request.add_header('Authorization', 'Basic %s' % auth_hash.decode())
                download_object = request.urlopen(download_request)
                download_info = download_object.info()
                print(download_info)

                out_file = self.parm_hash.deploy_dir + "/runner.jar"
                with open(out_file, "wb") as descriptor:
                    file_size = int(download_info["Content-Length"])
                    print("Downloading: %s Bytes: %s" % (out_file, file_size))

                    downloaded_so_far = 0
                    tx_size = 8192
                    while True:
                        buffer = download_object.read(tx_size)
                        if not buffer:
                            print("download: NO MORE BYTES IN STREAM")
                            break

                        downloaded_so_far += len(buffer)
                        descriptor.write(buffer)
                        status = r"%10d  [%3.2f%%]" % (downloaded_so_far, downloaded_so_far * 100. / file_size)
                        status = status + chr(8)*(len(status)+1)
                        print(status)

                print("Checking Binary Hashes....")
                md5Hash, sha1Hash = self._compute_hashes(out_file)
                print("MD5 %s == %s" % (md5Digest, md5Hash))
                print("SHA1 %s == %s" % (shaDigest, sha1Hash))

                if not all([md5Digest == md5Hash, shaDigest == sha1Hash]):
                    raise FileCorruptedException("Hashes do not match for downloaded artifact")
                else:
                    print("Hashes match: download is OK")

            else:
                raise FetchError("Got HTTP code %s" % self.search_results.code)
        except error.HTTPError as url_exception:
            raise FetchError(url_exception.__str__())

# Main Entrypoint
if __name__=="__main__" :
    nexus_parser = Parser()

    try:
        task_options = nexus_parser.parse()
        print(dir(task_options))
        downloader = NexusDownloader(task_options)

        downloader.build_search_parameters()
        downloader.pull_artifact()

        sys.exit(0)
    except MalformedArgumentException as malformed_argument:
        print(malformed_argument)
        sys.exit(-1)
    except MissingArgumentException as missing_argument:
        print(missing_argument)
        sys.exit(-2)
    except InvalidRepoException as invalid_repo:
        print(invalid_repo)
        sys.exit(-3)
    except InvalidDeployPathException as invalid_target:
        print(invalid_target)
        sys.exit(-4)
    except FetchError as http_fetch_error:
        print(http_fetch_error)
        sys.exit(-5)