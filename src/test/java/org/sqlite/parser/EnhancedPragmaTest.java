package org.sqlite.parser;

import org.junit.Test;
import org.sqlite.parser.ast.Select;

public class EnhancedPragmaTest {
	@Test
	public void table_info() throws Exception {
		final String sql = "CREATE TABLE contract (\n" +
				"  id INTEGER PRIMARY KEY NOT NULL,\n" +
				"  _number TEXT NOT NULL,\n" +
				"  title TEXT NOT NULL,\n" +
				"  amendment TEXT,\n" +
				"  billing_address TEXT NOT NULL, -- seems useless to decompose it\n" +
				"  member_id INTEGER NOT NULL,\n" +
				"  client_id INTEGER NOT NULL,\n" +
				"  average_daily_rate INTEGER NOT NULL,\n" +
				"  saturday_comp_rate INTEGER, -- TODO (Type, Rate) dedicated table?\n" +
				"  sunday_comp_rate INTEGER,\n" +
				"  holiday_comp_rate INTEGER,\n" +
				"  night_comp_rate INTEGER,\n" +
				"  saturday_on_call_rate INTEGER,\n" +
				"  sunday_on_call_rate INTEGER,\n" +
				"  holiday_on_call_rate INTEGER,\n" +
				"  night_on_call_rate INTEGER,\n" +
				"  start_date INTEGER NOT NULL,\n" +
				"  end_date INTEGER,\n" +
				"  version INTEGER NOT NULL,\n" +
				"  FOREIGN KEY (member_id) REFERENCES member(id),\n" +
				"  FOREIGN KEY (client_id) REFERENCES client(id)\n" +
				");";
		Select select = EnhancedPragma.tableInfo(null, "contract", sql);
		final String tableInfo = select.toSql();
		Parser.parse(tableInfo);
	}
}