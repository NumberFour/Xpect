package org.xpect.tests.runner.memory;

import static org.apache.log4j.Logger.getLogger;
import static org.xpect.runner.XpectTestFiles.FileRoot.CLASS;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.xpect.lib.XpectTestResultTest;
import org.xpect.runner.XpectRunner;
import org.xpect.runner.XpectSuiteClasses;
import org.xpect.runner.XpectTestFiles;

@RunWith(XpectRunner.class)
@XpectSuiteClasses(XpectTestResultTest.class)
@XpectTestFileReplicatingURIProvider(filesToReplicate = { "memory.xt" })
public class MemoryRunnerTest {

	private static final Logger LOGGER = getLogger(MemoryRunnerTest.class);
	
	@AfterClass
	public static void afterClass() {
		cleanUpTestFiles();
	}

	private static Collection<URI> getAllTestFiles() {
		return new XpectTestFiles.Builder().relativeTo(CLASS).create(MemoryRunnerTest.class).getAllURIs();
	}

	private static Collection<String> getFilesToReplicate(final XpectTestFileReplicatingURIProvider provider) {
		final Collection<String> filenamesToReplicate = new HashSet<String>();
		for (int i = 0; i < provider.filesToReplicate().length; i++) {
			filenamesToReplicate.add(provider.filesToReplicate()[i]);
		}
		return filenamesToReplicate;
	}
	
	private static void cleanUpTestFiles() {
		LOGGER.info("Cleaning up obsolete replicated test files...");
		final XpectTestFileReplicatingURIProvider[] providers = 
				MemoryRunnerTest.class.getAnnotationsByType(XpectTestFileReplicatingURIProvider.class);
		
		for (final XpectTestFileReplicatingURIProvider provider : providers) {
			final Collection<String> filesToReplicate = getFilesToReplicate(provider);
			for (final URI uri : getAllTestFiles()) {
				try {
					final File file = new File(new java.net.URI(uri.toString()));
					if (file.exists() && file.isFile()) {
						if (!filesToReplicate.contains(file.getName())) {
							if (!file.delete()) {
								LOGGER.error("Error while deleting test file: " + file.getPath());
							}
						}
					}
				} catch (final URISyntaxException e) {
					LOGGER.warn("Cannot find file for URI: " + uri);
				}
			}
		}
		LOGGER.info("Obsolete test file clean up successfully finished.");
	}
	
}
