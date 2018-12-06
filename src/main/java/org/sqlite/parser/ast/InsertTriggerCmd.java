package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.commaNames;
import static org.sqlite.parser.ast.ToSql.doubleQuote;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class InsertTriggerCmd implements TriggerCmd {
	public final ResolveType orConflict;
	public final String tblName;
	public final List<String> columns;
	public final Select select;
	public final Upsert upsert;

	public InsertTriggerCmd(ResolveType orConflict,
			String tblName,
			List<String> columns,
			Select select,
			Upsert upsert) {
		this.orConflict = orConflict;
		this.tblName = requireNonNull(tblName);
		this.columns = columns;
		this.select = requireNonNull(select);
		this.upsert = upsert;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (ResolveType.Replace == orConflict) {
			a.append("REPLACE");
		} else {
			a.append("INSERT");
			if (orConflict != null) {
				a.append(" OR ");
				orConflict.toSql(a);
			}
		}
		a.append(" INTO ");
		doubleQuote(a, tblName);
		if (isNotEmpty(columns)) {
			a.append(" (");
			commaNames(a, columns);
			a.append(')');
		}
		a.append(' ');
		select.toSql(a);
		if (upsert != null) {
			upsert.toSql(a);
		}
	}
}
