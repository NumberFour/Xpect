package org.xpect.xtext.lib.setup.workspace;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.xpect.setup.XpectSetupComponent;
import org.xpect.xtext.lib.setup.FileSetupContext;
import org.xpect.xtext.lib.util.IFileUtil;

@XpectSetupComponent
public class File implements IResourceFactory<IFile, IContainer> {

	private final org.xpect.xtext.lib.setup.generic.File delegate;

	public File(org.xpect.xtext.lib.setup.generic.File file) {
		delegate = file;
	}

	public File() {
		delegate = new org.xpect.xtext.lib.setup.generic.File();
	}

	public File(String name) {
		delegate = new org.xpect.xtext.lib.setup.generic.File(name);
	}

	public IFile create(FileSetupContext ctx, IContainer container, Workspace.Instance instance) throws IOException {
		return IFileUtil.create(container, delegate.getLocalURI(ctx), delegate.getContents(ctx));
	}

	public void setFrom(String name) {
		delegate.setFrom(name);
	}

}
