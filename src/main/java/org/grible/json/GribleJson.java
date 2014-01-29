package org.grible.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.grible.settings.GlobalSettings;

import com.google.gson.Gson;

public class GribleJson {
	private String filePath;
	private IdPathPair[] pairs;

	public List<IdPathPair> getIdPathPairs() {
		List<IdPathPair> pairList = new ArrayList<>();
		for (IdPathPair pair : pairs) {
			pairList.add(pair);
		}
		return pairList;
	}

	public void setIdPathPairs(List<IdPathPair> pairList) {
		IdPathPair[] pairArray = new IdPathPair[pairList.size()];
		for (int i = 0; i < pairList.size(); i++) {
			pairArray[i] = pairList.get(i);
		}
		this.pairs = pairArray;
	}
	
	public int addPath(String path) {
		int maxId = getMaxId();
		List<IdPathPair> pairs = getIdPathPairs();
		int newId = maxId + 1;
		pairs.add(new IdPathPair(newId, path));
		setIdPathPairs(pairs);
		return newId;
	}
	
	private int getMaxId() {
		List<IdPathPair> pairs = getIdPathPairs();
		int maxId = 0;
		for (IdPathPair pair : pairs) {
			if (maxId < pair.getId()) {
				maxId = pair.getId();
			}
		}
		return maxId;
	}

	public void setFilePath(String path) {
		this.filePath = path + File.separator + "grible.json";
	}

	public String getFilePath() {
		return filePath;
	}

	public void save() throws Exception {
		FileWriter fw = new FileWriter(filePath);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(new Gson().toJson(this));
		bw.close();
	}

	public GribleJson read() throws Exception {
		FileReader fr = new FileReader(GlobalSettings.getInstance().getConfigJsonFilePath());
		BufferedReader br = new BufferedReader(fr);
		GribleJson gribleJson = new Gson().fromJson(br, GribleJson.class);
		br.close();
		setIdPathPairs(gribleJson.getIdPathPairs());
		setFilePath(gribleJson.getFilePath());
		return this;
	}
}
