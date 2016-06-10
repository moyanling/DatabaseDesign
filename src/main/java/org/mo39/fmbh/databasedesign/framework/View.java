package org.mo39.fmbh.databasedesign.framework;

public abstract class View {

	public static void newView(Viewable obj) {
	  print(obj.getView());
	}

	public static void newView(String str) {
		print(str);
	}

	public static interface Viewable {

		String getView();

	}

	private static void print(Object obj) {
		System.out.print(obj);
	}


}
