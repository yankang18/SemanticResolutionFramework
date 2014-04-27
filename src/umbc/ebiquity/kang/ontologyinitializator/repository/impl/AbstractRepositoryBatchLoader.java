package umbc.ebiquity.kang.ontologyinitializator.repository.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import umbc.ebiquity.kang.ontologyinitializator.repository.interfaces.IReadOnlyRepository;
import umbc.ebiquity.kang.ontologyinitializator.utilities.Debugger;

public abstract class AbstractRepositoryBatchLoader implements IReadOnlyRepository {
	
	private String _repositoryFullPath;
	protected AbstractRepositoryBatchLoader(String repositoryFullPath){
		_repositoryFullPath = repositoryFullPath;
	}

	@Override
	public boolean loadRepository() {

		Debugger.print("Load Dir: %s ", _repositoryFullPath);
		File dir = new File(_repositoryFullPath);
		for (File file : dir.listFiles()) {
			if (!file.isHidden() && file.isFile()) {
				Debugger.print("Load File: %s ", file.getName());
				this.loadRecords(file);
			}
		}
		return false;
	}

	private boolean loadRecords(File file) {

		BufferedReader reader = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				this.loadRecord(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	protected abstract void loadRecord(String line);

}
