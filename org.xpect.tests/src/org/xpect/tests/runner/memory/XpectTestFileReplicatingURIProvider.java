package org.xpect.tests.runner.memory;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.xpect.runner.XpectURIProvider;

@Retention(RUNTIME)
@Target(TYPE)
@XpectURIProvider(XpectTestFileReplicator.class)
public @interface XpectTestFileReplicatingURIProvider {

	/**
	 * Array of the file names that has to be replicated for the tests.
	 * @return an array of test file names. Empty by default.
	 */
	String[] filesToReplicate() default "";
	
	/**
	 * The number of replicant test files to create.
	 * @return the number of replicant test files to create.
	 */
	int numberOfReplications() default 1000;
	
}


