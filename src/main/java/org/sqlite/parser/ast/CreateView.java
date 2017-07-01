package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.sqlite.parser.ast.ToSql.comma;
import static org.sqlite.parser.ast.ToSql.isNotEmpty;

public class CreateView implements Stmt {
	public final boolean temporary;
	public final boolean ifNotExists;
	public final QualifiedName viewName;
	public final List<IndexedColumn> columns;
	public final Select select;

	public CreateView(boolean temporary,
			boolean ifNotExists,
			QualifiedName viewName,
			List<IndexedColumn> columns,
			Select select) {
		this.temporary = temporary;
		this.ifNotExists = ifNotExists;
		this.viewName = requireNonNull(viewName);
		this.columns = columns;
		this.select = requireNonNull(select);
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		a.append("CREATE ");
		if (temporary) {
			a.append("TEMP ");
		}
		a.append("VIEW ");
		if (ifNotExists) {
			a.append("IF NOT EXISTS ");
		}
		viewName.toSql(a);
		if (isNotEmpty(columns)) {
			a.append(" (");
			comma(a, columns);
			a.append(')');
		}
		a.append(" AS ");
		select.toSql(a);
	}
}