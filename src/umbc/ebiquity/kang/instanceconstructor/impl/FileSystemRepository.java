package umbc.ebiquity.kang.instanceconstructor.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel;
import umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModelRepository;
import umbc.ebiquity.kang.ontologyinitializator.repository.FileRepositoryParameterConfiguration;
import umbc.ebiquity.kang.ontologyinitializator.repository.MappingInfoSchemaParameter;

public class FileSystemRepository implements IInstanceDescriptionModelRepository {
	
	private String tripleFullPath;
	private ModelSaveSupport saveSupport;
	private ModelLoadSupport loadSupport;
	
	public FileSystemRepository(){
		saveSupport = new ModelSaveSupport();
		loadSupport = new ModelLoadSupport();
		tripleFullPath = FileRepositoryParameterConfiguration.getTripleRepositoryDirectoryFullPath();
		System.out.println("Full Path: " + tripleFullPath);
	}

	/*
	 * (non-Javadoc)
	 * @see umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModelRepository#save(umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModel, java.lang.String)
	 */
	@Override
	public boolean save(IInstanceDescriptionModel model, String repositoryName) {
		System.out.println("Saving Extracted Triples ...");
		
		RecordsHolder recordsHolder = saveSupport.record(model);
		if (recordsHolder.hasRecords()) {
			System.out.println("Triples Extracted");
			String filePath = tripleFullPath;
			String fileName = repositoryName;
			String fileFullName = filePath + fileName;
			return this.save(fileFullName, recordsHolder);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see umbc.ebiquity.kang.instanceconstructor.IInstanceDescriptionModelRepository#load(java.lang.String)
	 */
	@Override
	public IInstanceDescriptionModel load(String repositoryName) {
		String fileFullName = tripleFullPath + repositoryName;
		try {
			return loadModel(fileFullName);
		} catch (IOException e) {
			// TODO: throw unchecked exception
			return null;
		}
	}

	private boolean save(String fileFullName, RecordsHolder recordsHolder) {
		List<String> records = recordsHolder.getRecordsAsString();
		StringBuilder triplesStringBuilder = new StringBuilder();
		for (String record : records) {
			triplesStringBuilder.append(record);
			triplesStringBuilder.append(MappingInfoSchemaParameter.LINE_SEPARATOR);
		}
		return save(fileFullName, triplesStringBuilder.toString());
	}

	private boolean save(String fileFullName, String tripleString) {

		File file = new File(fileFullName);
		Writer writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(tripleString);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Load triples from the file system. This method will also populate various
	 * Sets and Maps with these triples. If succeed, return true. Otherwise
	 * return false
	 * 
	 * @param fileFullName
	 *            the name of the file store triples.
	 * @throws IOException
	 */
	private IInstanceDescriptionModel loadModel(String fileFullName) throws IOException {
		System.out.println("Loading Triples from " + fileFullName);
		File file = new File(fileFullName);
		BufferedReader reader = null;
		List<String> recordsAsString = new ArrayList<String>();
		try {
			String line;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				recordsAsString.add(line);
			}

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
		return loadSupport.load(recordsAsString);
	}
}
