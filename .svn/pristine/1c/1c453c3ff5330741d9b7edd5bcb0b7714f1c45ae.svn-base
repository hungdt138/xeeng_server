package com.tv.xeeng.databaseDriven;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;


public class ImportData {
	public static void main(String[] args) {
		ImportData d = new ImportData();
		ArrayList<ArrayList<String>> data = d
				.readFolder("data", "data");
		d.insertIntoDB(data);
	}
	public ImportData() {
	}

	final static Charset ENCODING = StandardCharsets.UTF_8;

	public ArrayList<ArrayList<String>> readFolder(String path, String name) {
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		File folder = new File(path);
		if (folder != null) {
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles != null) {
				for (int i = 0; i < listOfFiles.length; i++) {
					File f = listOfFiles[i];
					if (f.isFile()) {
						res.add(readTextFile(f.getPath(), name));
					} else
						res.addAll(readFolder(f.getPath(), f.getName()));
				}
			}
		}
		return res;
	}

	private ArrayList<String> readTextFile(String aFileName, String folderName) {
		Path path = Paths.get(aFileName);
		ArrayList<String> data = new ArrayList<String>();

		try {
			data.add(folderName);
			Scanner scanner = new Scanner(path, ENCODING.name());

			while (scanner.hasNextLine()) {
				data.add(scanner.nextLine().trim());
			}
			scanner.close();
			return data;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
        
	public void insertIntoDB(ArrayList<ArrayList<String>> data) {
		Connection con = DBPoolConnection.getConnection();
		String query = "{call InsertQuestion(?,?,?,?,?,?,?,?) }";
		try {
			CallableStatement cs = con.prepareCall(query);
			int i = 0;
			for (ArrayList<String> s : data) {
				if (s.size() == 7) {
					i++;
					cs.clearParameters();
					
                                        String detail = s.get(1);
                                        int index = detail.indexOf(".");
                                        String inputDetail = detail.substring(index+1);
                                        
                                        cs.setString(1, inputDetail);
					cs.setString(2, s.get(2));
					cs.setString(3, s.get(3));
					cs.setString(4, s.get(4));
					cs.setString(5, s.get(5));
                                        int answer = 0;
                                        String strAnswer = s.get(6).trim();
                                        if(strAnswer.equals("a"))
                                        {
                                            answer = 1;
                                        } else if(strAnswer.equals("b"))
                                        {
                                            answer = 2;
                                        } else if(strAnswer.equals("c"))
                                        {
                                            answer = 3;
                                        } else if(strAnswer.equals("d"))
                                        {
                                            answer = 4;
                                        }
                                        
                                        
                                        
					cs.setInt(6, answer);// answer
					cs.setInt(7, Integer.parseInt(s.get(0)));// level
					cs.setInt(8, 1);// domain
					System.out.println("insert cau thu:"+ i);
					cs.execute();
				}
			}
			cs.close();
                
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
