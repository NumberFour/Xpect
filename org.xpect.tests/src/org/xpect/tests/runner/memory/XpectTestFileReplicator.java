package org.xpect.tests.runner.memory;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createTempFile;
import static java.util.UUID.randomUUID;
import static org.junit.Assume.assumeTrue;
import static org.xpect.Environment.STANDALONE_TEST;
import static org.xpect.runner.XpectTestFiles.FileRoot.CLASS;
import static org.xpect.util.EnvironmentUtil.ENVIRONMENT;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.common.util.URI;
import org.xpect.runner.IXpectURIProvider;
import org.xpect.runner.XpectTestFiles;

public class XpectTestFileReplicator implements IXpectURIProvider {

	private final IXpectURIProvider delegate;
	private final XpectTestFileReplicatingURIProvider ctx;
	
	/*default*/ XpectTestFileReplicator(final Class<?> clazz, final XpectTestFileReplicatingURIProvider ctx) 
			throws URISyntaxException, IOException {
		
		assumeTrue(STANDALONE_TEST == ENVIRONMENT);
		
		this.ctx = ctx;
		this.delegate = new XpectTestFiles.Builder().relativeTo(CLASS).create(clazz);
		replicateTestFiles();
	}
	
	public Collection<URI> getAllURIs() {
		return delegate.getAllURIs();
	}

	public URI resolveURI(final URI base, final String newURI) {
		return delegate.resolveURI(base, newURI);
	}

	public URI deresolveToProject(final URI uri) {
		return delegate.deresolveToProject(uri);
	}

	private void replicateTestFiles() throws URISyntaxException, IOException {
		final Collection<String> filenamesToReplicate = new HashSet<String>();
		for (int i = 0; i < ctx.filesToReplicate().length; i++) {
			filenamesToReplicate.add(ctx.filesToReplicate()[i]);
		}
		for (final URI uri : getAllURIs()) {
			if (filenamesToReplicate.contains(uri.lastSegment())) {
				final File originalFile = new File(new java.net.URI(uri.toString()));
				final Path originalFilePath = Paths.get(originalFile.toURI());
				for (int i = 0; i < ctx.numberOfReplications(); i++) {
					final File replicant = createTempFile(originalFilePath.getParent(), randomUUID().toString(), ".xt").toFile();
					replicant.deleteOnExit();
					copyContent(originalFilePath, replicant);
				}
			}
		}
	}

	private void copyContent(final Path originalFilePath, final File replicant) throws IOException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(replicant);
			copy(originalFilePath, os);
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (final IOException e) {
					try {
						os.close();
					} catch (final IOException e1) {
						//intentionally ignored
					}
					throw e;
				}
			}
		}
	}

}
