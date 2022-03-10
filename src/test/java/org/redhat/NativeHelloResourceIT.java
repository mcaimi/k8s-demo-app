package org.redhat;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
class NativeHelloResourceIT extends HelloResourceTest {

    // Execute the same tests but in native mode.
}