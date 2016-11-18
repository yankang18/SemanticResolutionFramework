package umbc.ebiquity.kang.instanceconstructor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

public class RecordsHolder {

	private List<Map<String, String>> records;
	private List<String> recordsAsString;
	private int numberOfRelations;

	RecordsHolder() {
		records = new ArrayList<Map<String, String>>();
		recordsAsString = new ArrayList<String>();
	}

	public void addRecord(Map<String, String> record) {
		records.add(record);
		recordsAsString.add(JSONValue.toJSONString(record));
	}

	public void setNumOfRelations(int numberOfRelations) {
		this.numberOfRelations = numberOfRelations;
	}

	public List<Map<String, String>> getRecords() {
		return records;
	}

	public List<String> getRecordsAsString() {
		return recordsAsString;
	}

	public int getNumberOfRelations() {
		return numberOfRelations;
	}

	public boolean hasRecords() {
		return records.size() > 0 ? true : false;
	}

}
