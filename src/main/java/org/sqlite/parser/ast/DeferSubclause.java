package org.sqlite.parser.ast;

import java.io.IOException;
import java.sql.DatabaseMetaData;

// FIXME not really a ColumnConstraint but we need it to parse...
public class DeferSubclause extends ColumnConstraint {
	public final boolean deferrable;
	public final InitDeferredPred initDeferred;

	public DeferSubclause(boolean deferrable, InitDeferredPred initDeferred) {
		super(null);
		this.deferrable = deferrable;
		this.initDeferred = initDeferred;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		if (!deferrable) {
			a.append("NOT ");
		}
		a.append("DEFERRABLE");
		if (initDeferred != null) {
			a.append(' ');
			initDeferred.toSql(a);
		}
	}

	/**
	 * @return {@link DatabaseMetaData#importedKeyNotDeferrable}, ...
	 */
	public int getDeferrability() {
		if (!deferrable) {
			return DatabaseMetaData.importedKeyNotDeferrable;
		}
		if (initDeferred == InitDeferredPred.InitiallyDeferred) {
			return DatabaseMetaData.importedKeyInitiallyDeferred;
		}
		return DatabaseMetaData.importedKeyInitiallyDeferred;
	}
}
