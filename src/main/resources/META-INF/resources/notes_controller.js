var app = angular.module("NotesManagement", []);

//Controller Part
app.controller("NotesManagementController", function ($scope, $http) {

  //Initialize page with default data which is blank in this example
  $scope.notes = [];

  $scope.form = {
    id: -1,
    name: "",
    contents: ""
  };

  //Now load the data from server
  _refreshPageData();

  //HTTP POST/PUT methods for add/edit notes
  $scope.update = function () {
    var method = "";
    var url = "";
    var data = {};
    if ($scope.form.id == -1) {
      //Id is absent so add the new note - POST operation
      method = "POST";
      url = '/notes/publish';
      data.name = $scope.form.name;
      data.contents = $scope.form.contents;
    } else {
      //If Id is present, it's an edit - PUT operation
      method = "PUT";
      url = '/notes/' + $scope.form.id;
      data.name = $scope.form.name;
      data.contents = $scope.form.contents;
    }

    $http({
      method: method,
      url: url,
      data: angular.toJson(data),
      headers: {
        'Content-Type': 'application/json'
      }
    }).then(_success, _error);
  };

  //HTTP DELETE- delete note by id
  $scope.remove = function (note) {
    $http({
      method: 'DELETE',
      url: '/notes/' + note.id
    }).then(_success, _error);
  };

  //In case of an edit opetation, populate form with note data
  $scope.edit = function (note) {
    $scope.form.name = note.name;
    $scope.form.contents = note.contents;
    $scope.form.id = note.id;
  };

    /* Private Methods */
  //HTTP GET- get all notes
  function _refreshPageData() {
    $http({
      method: 'GET',
      url: '/notes/all'
    }).then(function successCallback(response) {
      $scope.notes = angular.fromJson(response.data);
    }, function errorCallback(response) {
      console.log(response.statusText);
    });
  }

  function _success(response) {
    _refreshPageData();
    _clearForm()
  }

  function _error(response) {
    alert(response.data.message || response.statusText);
  }

  //Clear the form
  function _clearForm() {
    $scope.form.name = "";
    $scope.form.contents = "";
    $scope.form.id = -1;
  }
});