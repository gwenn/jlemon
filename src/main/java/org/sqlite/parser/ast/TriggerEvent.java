package org.sqlite.parser.ast;

import java.io.IOException;
import java.util.List;

import static org.sqlite.parser.ast.ToSql.doubleQuote;

public class TriggerEvent implements ToSql {
	public final TriggerEventType type;
	public final List<String> colNames; // only for event = UpdateOf

	public static TriggerEvent from(int tt) {
		return new TriggerEvent(TriggerEventType.from(tt), null);
	}

	public TriggerEvent(TriggerEventType type, List<String> colNames) {
		this.type = type;
		this.colNames = colNames;
	}

	@Override
	public void toSql(Appendable a) throws IOException {
		type.toSql(a);
		if (TriggerEventType.UpdateOf == type) {
			for (int i = 0; i < colNames.size(); i++) {
				if (i == 0) {
					a.append(' ');
				} else {
					a.append(", ");
				}
				doubleQuote(a, colNames.get(i));
			}
		}
	}
}
