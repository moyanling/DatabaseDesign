package org.mo39.fmbh.DatabaseDesign.main;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.collect.Lists;

public class Main {

	private static Map<String, String> commandsMap;

	private static Scanner console = new Scanner(System.in);

	public static void main(String[] args) throws ParseException {
		Options opts = new Options();
		CommandLineParser parser = new DefaultParser();
		opts.addOption("h", "help", false, "Help description.");
		opts.addOption("r", "run", false, "Start to run database.");
		opts.addOption("a", "all", false, "Show all supported SQL commands.");

		CommandLine cl;
		cl = parser.parse(opts, args);
		if (cl.hasOption('r')) {
			welcome();
		} else if (cl.hasOption('a')) {
			showCommandList();
		} else {
			new HelpFormatter().printHelp("Show Options", opts);
		}

	}

	private static void showCommandList() {

	}

	private static void parseSQLCommand(String args) {
		if (args == "EXIT") {
			console.close();
			System.exit(0);
		} else if (args == "SHOW SCHEMAS") {
			print("I'am showing schemas");
		} else if (args.startsWith("USE")) {
			print("I'm choosing a schema");
		} else if (args == "SHOW TABLES") {
			print("I'm showing tables");

		} else if (args.startsWith("CREATE SCHEMA")) {

		}
	}

	private static void welcome() {
		print("+------------------------------\n");
		print("+Welcome to mo39.fbmh.Database.\n");
		print("+------------------------------");

		ArrayList<String> argList = Lists.newArrayList();
		while (true) {
			// ---------------
			print("\n\nmo39.fbmh.sql> ");
			// ---------------
			Scanner scan = new Scanner(System.in);
			for (int i = 0; i <= 10; i++) {
				argList.add(scan.next());
			}
			// ---------------
			print(argList);
		}
	}

	public static void print(Object obj) {

		System.out.print(obj);
	}

}
