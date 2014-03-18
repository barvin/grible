package org.grible.dbmigrate;

import java.util.ArrayList;
import java.util.List;

import org.grible.dao.PostgresDao;
import org.grible.dbmigrate.oldmodel.OldKey;
import org.grible.dbmigrate.oldmodel.OldRow;
import org.grible.dbmigrate.oldmodel.OldValue;
import org.grible.model.Table;
import org.grible.model.TableType;
import org.grible.model.json.Key;
import org.grible.model.json.KeyType;

public class MigrationActions {
	public static void moveDataToKeysAndValuesColumns() throws Exception {
		PostgresDao dao = new PostgresDao();
		List<Table> tables = dao.getAllTables();

		for (Table table : tables) {
			List<OldKey> oldKeys = dao.getOldKeys(table.getId());
			Key[] newKeys = new Key[oldKeys.size()];
			for (int i = 0; i < newKeys.length; i++) {
				KeyType type = KeyType.TEXT;
				if (oldKeys.get(i).getReferenceTableId() != 0) {
					if (dao.getTable(oldKeys.get(i).getReferenceTableId()).getType() == TableType.STORAGE) {
						type = KeyType.STORAGE;
					} else {
						type = KeyType.ENUMERATION;
					}
				}
				newKeys[i] = new Key(oldKeys.get(i).getName(), type, oldKeys.get(i).getReferenceTableId());
			}

			List<OldRow> rows = dao.getOldRows(table.getId());
			String[][] newValues = new String[rows.size()][newKeys.length];
			for (int i = 0; i < rows.size(); i++) {
				ArrayList<OldValue> rowValues = dao.getOldValues(rows.get(i));
				for (int j = 0; j < rowValues.size(); j++) {
					newValues[i][j] = rowValues.get(j).getValue();
				}
			}
			
			table.setKeys(newKeys);
			table.setValues(newValues);
			dao.updateTable(table);
		}
	}
}
