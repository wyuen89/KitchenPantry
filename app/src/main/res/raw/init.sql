PRAGMA foreign_keys = 1;
CREATE TABLE IF NOT EXISTS Recipe(RecID INTEGER PRIMARY KEY, Name TEXT NOT NULL, Cuisine TEXT, PrepTime INTEGER, CookTime INTEGER);
CREATE TABLE IF NOT EXISTS Ingredients(IngredientID INTEGER PRIMARY KEY, Name TEXT NOT NULL, Type TEXT);
CREATE TABLE IF NOT EXISTS RecIngredients(RecID INTEGER, IngredientID INTEGER, PRIMARY KEY(RecID, IngredientID), FOREIGN KEY(RecID) REFERENCES Recipe(RecID), FOREIGN KEY(IngredientID) REFERENCES Ingredients(IngredientID));
CREATE TABLE IF NOT EXISTS Instructions(RecID INTEGER, Step INTEGER NOT NULL, Instr TEXT, PRIMARY KEY(RecID, Step), FOREIGN KEY(RecID) REFERENCES Recipe(RecID));