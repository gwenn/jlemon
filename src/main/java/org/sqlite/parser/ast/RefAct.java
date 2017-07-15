package org.sqlite.parser.ast;

import java.io.IOException;
import java.sql.DatabaseMetaData;

public enum RefAct implements ToSql {
	SetNull,
	SetDefault,
	Cascade,
	Restrict,
	NoAction;

	@Override
	public void toSql(Appendable a) throws IOException {
		if (SetNull == this) {
			a.append("SET NULL");
		} else if (SetDefault == this) {
			a.append("SET DEFAULT");
		} else if (Cascade == this) {
			a.append("CASCADE");
		} else if (Restrict == this) {
			a.append("RESTRICT");
		} else if (NoAction == this) {
			a.append("NO ACTION");
		}
	}

	/**
	 * @return {@kink DatabaseMetaData#importedKeyNoAction}, ...
	 */
	public int getRule() {
		if (SetNull == this) {
			return DatabaseMetaData.importedKeySetNull;
		} else if (SetDefault == this) {
			return DatabaseMetaData.importedKeySetDefault;
		} else if (Cascade == this) {
			return DatabaseMetaData.importedKeyCascade;
		} else if (Restrict == this) {
			return DatabaseMetaData.importedKeyRestrict;
		} else if (NoAction == this) {
			return DatabaseMetaData.importedKeyNoAction;
		}
		throw new AssertionError();
	}
}
