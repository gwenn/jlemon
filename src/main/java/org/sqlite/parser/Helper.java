package org.sqlite.parser;

import java.util.ArrayList;
import java.util.List;

class Helper {
	static <E> List<E> append(List<E> lst, E item) {
		if (lst == null) {
			lst = new ArrayList<>();
		}
		lst.add(item);
		return lst;
	}
}
