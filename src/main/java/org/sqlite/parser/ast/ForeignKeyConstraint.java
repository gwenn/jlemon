package org.sqlite.parser.ast;

import java.sql.DatabaseMetaData;

public interface ForeignKeyConstraint {
	ForeignKeyClause getClause();
	DeferSubclause getDerefClause();

	/**
	 * @return {@link DatabaseMetaData#importedKeyNotDeferrable}, ...
	 */
	default int getDeferrability() {
		final DeferSubclause derefClause = getDerefClause();
		if (derefClause == null) {
			return DatabaseMetaData.importedKeyNotDeferrable;
		}
		return derefClause.getDeferrability();
	}
}