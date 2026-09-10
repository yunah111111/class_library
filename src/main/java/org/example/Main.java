package org.example;

import org.example.view.LibraryView;

import java.sql.SQLException;

// 프로그램 진입점
public class Main {
    public static void main(String[] args) throws SQLException {
        LibraryView libraryView = new LibraryView();
        libraryView.start();
    }
}