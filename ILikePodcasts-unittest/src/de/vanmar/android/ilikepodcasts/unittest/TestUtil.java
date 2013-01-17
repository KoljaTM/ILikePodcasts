package de.vanmar.android.ilikepodcasts.unittest;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class TestUtil {

	public static void mockUrl(final URL urlMock, final String filename)
			throws IOException {
	}

	public static URL getMockUrl(final String filename) throws IOException {
		final File file = new File("../ILikePodcasts-unittest/testdata/"
				+ filename);
		assertTrue("Mock HTML File " + filename + " not found", file.exists());
		final URLConnection mockConnection = mock(URLConnection.class);
		given(mockConnection.getInputStream()).willReturn(
				new FileInputStream(file));

		final URLStreamHandler handler = new URLStreamHandler() {

			@Override
			protected URLConnection openConnection(final URL arg0)
					throws IOException {
				return mockConnection;
			}
		};
		final URL url = new URL("http://foo.bar", "foo.bar", 80, "", handler);
		return url;
	}

}
