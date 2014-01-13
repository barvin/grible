package org.grible.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.grible.model.Product;
import org.grible.settings.GlobalSettings;

import com.google.gson.Gson;

public class ConfigJson {
	private Product[] products;
	private boolean isTooltipOnClick;

	public List<Product> getProducts() {
		List<Product> productList = new ArrayList<>();
		for (Product product : products) {
			productList.add(product);
		}
		return productList;
	}

	public void setProducts(List<Product> productList) {
		Product[] productArray = new Product[productList.size()];
		for (int i = 0; i < productList.size(); i++) {
			productArray[i] = productList.get(i);
		}
		this.products = productArray;
	}

	public boolean isTooltipOnClick() {
		return isTooltipOnClick;
	}

	public void setTooltipOnClick(boolean isTooltipOnClick) {
		this.isTooltipOnClick = isTooltipOnClick;
	}

	public void save() throws Exception {
		FileWriter fw = new FileWriter(GlobalSettings.getInstance().getConfigJsonFilePath());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(new Gson().toJson(this));
		bw.close();
	}

	public ConfigJson read() throws Exception {
		FileReader fr = new FileReader(GlobalSettings.getInstance().getConfigJsonFilePath());
		BufferedReader br = new BufferedReader(fr);
		ConfigJson configJson = new Gson().fromJson(br, ConfigJson.class);
		br.close();
		setProducts(configJson.getProducts());
		setTooltipOnClick(configJson.isTooltipOnClick());
		return this;
	}
}
