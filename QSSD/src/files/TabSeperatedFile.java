package files;

import parse.ParseTable;

import java.util.ArrayList;

public abstract class TabSeperatedFile {

    abstract ArrayList<ArrayList<Object>> getContent();

    public ArrayList<ParseTable> getTables() {
        ArrayList<ParseTable> tabs = new ArrayList<>();
            ArrayList<ArrayList<ArrayList<Object>>> sheet = this.breakIntoContent(this.getContent());
            for (ArrayList<ArrayList<Object>> table: sheet) {
                if (table.size() >= 2) {
                    tabs.add(new ParseTable(table));
                }
            }
        return tabs;
    }

    private ArrayList<ArrayList<ArrayList<Object>>> breakIntoContent(ArrayList<ArrayList<Object>> content) {
        int xPos;
        int yPos = 0;
        int colSize = content.get(0).size();
        int rowSize = content.size();
        ArrayList<ArrayList<ArrayList<Object>>> results = new ArrayList<>();
        ArrayList<Object> tempRow;
        ArrayList<ArrayList<Object>> tempsection;
        int tempRowPos,tempColPos;
        while (yPos < rowSize-1) {
            xPos = 0;
            while (xPos < colSize-1) {
                if (content.get(yPos).size() != 0) {
                    if (content.get(yPos).get(xPos) != null) {
                        tempsection = new ArrayList<>();
                        tempRowPos = 0;
                        while (content.get(yPos + tempRowPos).get(xPos) != null) {
                            tempRow = new ArrayList<>();
                            tempColPos = 0;
                            while (content.get(yPos + tempRowPos).get(xPos + tempColPos) != null) {
                                tempRow.add(content.get(yPos + tempRowPos).get(xPos + tempColPos));
                                ArrayList<Object> row = content.get(yPos + tempRowPos);
                                row.set(xPos + tempColPos, null);
                                content.set(yPos + tempRowPos, row);
                                if (xPos + tempColPos < content.get(yPos + tempRowPos).size() - 1) {
                                    tempColPos++;
                                }
                            }
                            tempsection.add(tempRow);
                            if (yPos + tempRowPos < rowSize - 1 && content.get(yPos + tempRowPos + 1).size() != 0) {
                                tempRowPos++;
                            }
                        }
                        results.add(tempsection);
                    }
                }
                xPos++;
            }
            yPos++;
        }
        ArrayList<Object> row;
        for (ArrayList<ArrayList<Object>> table: results) {
            int rowGoal = table.get(0).size();
            for (int i = 0; i < table.size(); i++) {
                row = table.get(i);
                while (row.size() < rowGoal) {
                    row.add(null);
                }
                table.set(i,row);
            }
        }
        return results;
    }
}
