package org.grible.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class GribleJson {
	private String filePath;
	private IdPathPair[] pairs;

	public IdPathPair[] getPairs() {
		return pairs;
	}

	public void setPairs(IdPathPair[] pairs) {
		this.pairs = pairs;
	}

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
		this.filePath = path;
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
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		GribleJson gribleJson = new Gson().fromJson(br, GribleJson.class);
		br.close();
		setIdPathPairs(gribleJson.getIdPathPairs());
		setFilePath(gribleJson.getFilePath());
		return this;
	}

	public String getPathById(int id) {
		List<IdPathPair> pairs = getIdPathPairs();
		for (IdPathPair pair : pairs) {
			if (pair.getId() == id) {
				return pair.getPath();
			}
		}
		return null;
	}

	public int getIdByPath(String path) {
		List<IdPathPair> pairs = getIdPathPairs();
		for (IdPathPair pair : pairs) {
			if (pair.getPath().endsWith(path)) {
				return pair.getId();
			}
		}
		return 0;
	}

	public void deleteId(int id) {
		List<IdPathPair> pairs = getIdPathPairs();
		List<IdPathPair> newPairs = new ArrayList<>();
		for (IdPathPair pair : pairs) {
			if (pair.getId() != id) {
				newPairs.add(pair);
			}
		}
		setIdPathPairs(newPairs);
	}

	public void updatePath(int id, String path) {
		List<IdPathPair> pairs = getIdPathPairs();
		for (IdPathPair pair : pairs) {
			if (pair.getId() == id) {
				pair.setPath(path);
			}
		}
		setIdPathPairs(pairs);
	}
}
