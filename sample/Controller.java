package sample;

import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Controller {
    File saveLocation;
    ArrayList<File> pdflist = new ArrayList<>();
    ArrayList<String> taskListAddPages = new ArrayList<>();
    String savePath = "";
    String decryptSavePath = "";
    File savePathWatermark;
    File savePathStamp;
    File filetoDecrypt;
    File decryptSaveFile;
    String operation = "";
    String rotationAngle = "";
    boolean notInit = true;
    boolean popnotInit = true;
    int selectedIndex;
    ObservableList pdfs = FXCollections.observableArrayList();
    ObservableList options = FXCollections.observableArrayList();
    boolean hasPassword = false;
    String password = "";
    String userPassword = "";
    boolean watermarkSet = false;
    boolean stampSet = false;
    FileChooser fileChooser;

    @FXML
    TextArea linesTextArea = new TextArea();
    @FXML
    TextField watermarkArea = new TextField();
    @FXML
    TextField stampArea = new TextField();
    @FXML
    TextField pagesAdd = new TextField();
    @FXML
    TextField passwordArea = new TextField();
    @FXML
    TextField userPasswordArea = new TextField();
    @FXML
    CheckBox allowPrinting = new CheckBox();
    @FXML
    TextField fileToDecrypt = new TextField();
    @FXML
    TextField decryptSave = new TextField();
    @FXML
    TextField passwordAreaD = new TextField();
    @FXML
    TextField userPasswordAreaD = new TextField();
    @FXML
    RadioButton rbJoin;
    @FXML
    RadioButton rbBurst;
    @FXML
    RadioButton rbShuffle;

    @FXML
    RadioButton noRot;
    @FXML
    RadioButton eastRot;
    @FXML
    RadioButton southRot;
    @FXML
    RadioButton westRot;

    @FXML
    Text valid = new Text();
    @FXML
    Text correctInput = new Text();
    @FXML
    Text correctPass = new Text();
    @FXML
    Text correctPassDecrypt = new Text();

    @FXML
    ListView<ObservableList> mylist;

    ArrayList<String> hashKeys = new ArrayList<>();
    HashMap<String, ArrayList<String>> tasks = new HashMap<>();

    Stage popup;

    final ToggleGroup group = new ToggleGroup();
    final ToggleGroup popGroup = new ToggleGroup();
    Controller referenceToOther;


    public Controller() {
        mylist = new ListView<>();
        options.addAll("Select/Rotate Pages", "Delete"); //You don't see this at all but IT JUST WORKS!
        mylist.setCellFactory(ComboBoxListCell.forListView(options));
        fileChooser = new FileChooser();
    }

    @FXML
    public void handleMouseClick(MouseEvent mouseEvent) throws IOException {
        selectedIndex = pdfs.indexOf(mylist.getSelectionModel().getSelectedItem());
        System.out.println(pdfs.indexOf(mylist.getSelectionModel().getSelectedItem()));
        System.out.println("pdfs:" + pdfs.size() + "pdflist:" + pdflist.size());
        popup = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("popupoptions.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("popupoptions.fxml"));
        Parent root = (Parent)loader.load();
        popup.setScene(new Scene(root));
        Controller otherCont = loader.getController();
        popup.setTitle("Specify Tasks");
        popup.initModality(Modality.NONE);
        popup.initOwner(mylist.getScene().getWindow());
        otherCont.shareInformation(this, pdfs, pdflist, tasks, hashKeys, popup, selectedIndex);
        popup.show();
    }

    //Allows for information to be passed between two controllers
    public void shareInformation(Controller share, ObservableList observableList, ArrayList<File> arrayList, HashMap map, ArrayList<String> arrayList3, Stage sharepopup, int index) {
        referenceToOther = share;
        pdfs = observableList;
        pdflist = arrayList;
        popup = sharepopup;
        selectedIndex = index;
        tasks = map;
        hashKeys = arrayList3;
    }


    public void shareInformation(Controller share, ObservableList observableList, ArrayList<File> arrayList, HashMap map, ArrayList<String> arrayList3) {
        referenceToOther = share;
        pdfs = observableList;
        pdflist = arrayList;
        tasks = map;
        hashKeys = arrayList3;
    }

    //Initializes a set of radio buttons
    public void groupInit() {
        rbShuffle.setToggleGroup(group);

        rbBurst.setToggleGroup(group);

        rbJoin.setToggleGroup(group);
        notInit = false;
    }

    //Initializes the rotation radio buttons
    public void popupGroupInit() {
        noRot.setToggleGroup(popGroup);
        eastRot.setToggleGroup(popGroup);
        southRot.setToggleGroup(popGroup);
        westRot.setToggleGroup(popGroup);
        popnotInit = false;
    }

    public void showDecryptLoc() {
        fileToDecrypt.clear();
        fileToDecrypt.appendText(filetoDecrypt.getAbsolutePath());
    }

    public void showDecryptSaveLoc() {
        decryptSave.clear();
        decryptSave.appendText(decryptSavePath);
    }

    public void showStampLoc() {
        stampArea.clear();
        stampArea.appendText(savePathStamp.getAbsolutePath());
    }

    public void showWatermarkLoc() {
        watermarkArea.clear();
        watermarkArea.appendText(savePathWatermark.getAbsolutePath());
    }

    //Used for building the commmand line arguments, gathers all tasks
    private String showCatTasks() {
        StringBuilder builder = new StringBuilder();
        builder.append("");
        for (Map.Entry<String, ArrayList<String>> e : tasks.entrySet()) {
            String key = e.getKey();
            ArrayList<String> value = e.getValue();
            if (value.isEmpty()) {
                builder.append(calculateLetters(hashKeys.indexOf(key)) + "1-end ");

            }
            else {
                for (String task : value) {
                    builder.append(calculateLetters(hashKeys.indexOf(key)) + task + " ");
                }
            }
        }

        return builder.toString();
    }

    //Sets the password locally to be used on pdf creation
    public void addPassword(ActionEvent event) {
        correctPass.setText("");
        password = passwordArea.getText();
        userPassword = userPasswordArea.getText();
        hasPassword = true;
        passwordArea.clear();
        userPasswordArea.clear();
        if (password.equals("")) {
            correctPass.setText("Owner password needs to be set");
        }

    }

    //Method that actually modifies the command line argument to include the password
    private String putPasswordIn() {
        String result = "";
        if (hasPassword && !password.equals("")) {
            if (userPassword.equals("")) {
                result = " owner_pw " + password;
            }
            else {
                result = " owner_pw " + password + " user_pw " + userPassword;
                if (allowPrinting.isSelected()) {
                    result += " allow Printing";
                }
            }
        }
        return result;
    }

    //Removes the local password
    public void removePasswords(ActionEvent event) {
        hasPassword = false;
        password = "";
        userPassword = "";
        correctInput.setText("Cleared Local Passwords!");
    }

    //Method that adds tasks to the list (frontend)
    public void addTaskSelectpages(ActionEvent event) {
        valid.setText("");
       if (valid(pagesAdd.getText())) {
           System.out.println(pagesAdd.getText());
           addTaskToList(pagesAdd.getText());
       }
       else {
           valid.setText("Invalid Input");
       }
       pagesAdd.clear();
    }

    //Method that adds tasks to the list (backend)
    private void addTaskToList(String text) {
        Scanner scan = new Scanner(text);
        String sequence = calculateLetters(selectedIndex);
        String key = hashKeys.get(selectedIndex);
        ArrayList<String> temp = tasks.get(key);
        while (scan.hasNext()) {
            //taskListAddPages.add(sequence + scan.next());
            temp.add(scan.next() + rotationAngle);
        }
        tasks.put(key, temp);
    }

    //Does task syntax validation
    private boolean valid(String text) {
        Scanner scan = new Scanner(text);
        Scanner deep;
        String temp;
        boolean firstCheck;
        boolean isValid = true;
        while (scan.hasNext()) {
            temp = scan.next();
            deep = new Scanner(temp);
            deep.useDelimiter("-");
            firstCheck = true;
            while (deep.hasNext()) {
                String deepTemp = deep.next();
                if (deepTemp.equals("even")) {
                    if (firstCheck && deep.hasNext()) {
                        isValid = false;
                    }
                }
                else if (deepTemp.equals("odd")) {
                    if (firstCheck && deep.hasNext()) {
                        isValid = false;
                    }
                }
                else if (deepTemp.equals("end")) {
                    // Fall through
                }
                else {
                    try {
                        if (!firstCheck) {
                            boolean checkeven = deepTemp.contains("even");
                            boolean checkodd  = deepTemp.contains("odd");
                            String deepTempAlt = "";
                            if (checkodd) {
                               deepTempAlt = deepTemp.substring(deepTemp.length() - 3);
                               if (!deepTempAlt.equals("odd")) {
                                   isValid = false;
                               }
                               deepTemp = deepTemp.substring(0, deepTemp.length() - 3);
                            }
                            else if (checkeven) {
                                deepTempAlt = deepTemp.substring(deepTemp.length() - 4);
                                if (!deepTempAlt.equals("even")) {
                                    isValid = false;
                                }
                                deepTemp = deepTemp.substring(0, deepTemp.length() - 4);
                            }
                        }
                        if (!deepTemp.equals("end")) {
                            Integer tryAndSee = Integer.parseInt(deepTemp);
                        }
                    }
                    catch (Exception e) {
                        isValid = false;
                        System.out.println("Triggered in catch");
                    }
                }
                firstCheck = false;
            }
        }
        return isValid;
    }

    public void handleButtonAction(ActionEvent event) {
        if (notInit) {
            groupInit();
        }
        if (rbJoin.isSelected()) {
            operation = "cat";
        }
        else if (rbBurst.isSelected()) {
            operation = "burst";
        }
        else if (rbShuffle.isSelected()) {
            operation = "shuffle";
        }
    }

    public void handlePopupButtonAction(ActionEvent event) {
        if (popnotInit) {
            popupGroupInit();
        }
        if (noRot.isSelected()) {
            rotationAngle = "";
        }
        else if (eastRot.isSelected()) {
            rotationAngle = "right";
        }
        else if (southRot.isSelected()) {
            rotationAngle = "down";
        }
        else if (westRot.isSelected()) {
            rotationAngle = "left";
        }
    }

    //Creates the new pdf
    public void create() {
        ExecuteShellCommand exec = new ExecuteShellCommand();
        String command = "";
        String output = "";
        correctInput.setText("");
        if (!operation.equals("") && !savePath.equals("") && !pdflist.isEmpty()) {
            if (operation.equals("cat")) {
                if (watermarkSet) {
                    command = "pdftk " + pdflistText() + operation + " " + showCatTasks() + " output " + savePath + "TEMP.pdf";
                    exec.executeCommand(command);
                    System.out.println(command);
                    command = "pdftk " + savePath + "TEMP.pdf background " + savePathWatermark + " output " + savePath + ".pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                    File del = new File(savePath + "TEMP.pdf");
                    del.delete();
                }
                else if (stampSet) {
                    command = "pdftk " + pdflistText() + operation + " " + showCatTasks() + " output " + savePath + "TEMP.pdf";
                    exec.executeCommand(command);
                    System.out.println(command);
                    command = "pdftk " + savePath + "TEMP.pdf multistamp " + savePathWatermark + " output " + savePath + ".pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                    File del = new File(savePath + "TEMP.pdf");
                    del.delete();
                }
                else {
                    command = "pdftk " + pdflistText() + operation + " " + showCatTasks() + " output " + savePath + ".pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                }
            }
            else if (operation.equals("burst")) {
                if (watermarkSet) {
                    command = "pdftk " + pdflistText() + " cat " + showCatTasks() + " output " + savePath + "TEMP.pdf";
                    exec.executeCommand(command);
                    command = "pdftk " + savePath + "TEMP.pdf background " + savePathWatermark + " output " + savePath + ".pdf";
                    exec.executeCommand(command);
                    command = "pdftk " + savePath + ".pdf " + operation + " output " + savePath + "_%04d.pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                    File del = new File(savePath + ".pdf");
                    File del1 = new File(savePath + "TEMP.pdf");
                    del.delete();
                    del1.delete();

                }
                else if (stampSet) {
                    command = "pdftk " + pdflistText() + " cat " + showCatTasks() + " output " + savePath + "TEMP.pdf";
                    exec.executeCommand(command);
                    command = "pdftk " + savePath + "TEMP.pdf multistamp " + savePathWatermark + " output " + savePath + ".pdf";
                    exec.executeCommand(command);
                    command = "pdftk " + savePath + ".pdf " + operation + " output " + savePath + "_%04d.pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                    File del = new File(savePath + ".pdf");
                    File del1 = new File(savePath + "TEMP.pdf");
                    del.delete();
                    del1.delete();

                }
                else {
                    command = "pdftk " + pdflistText() + " cat " + showCatTasks() + " output " + savePath + ".pdf";
                    exec.executeCommand(command);
                    command = "pdftk " + savePath + ".pdf " + operation + " output " + savePath + "_%04d.pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                    File del = new File(savePath + ".pdf");
                    del.delete();
                }
            }
            else {
                if (watermarkSet) {
                    command = "pdftk " + pdflistText() + operation + " " + showCatTasks() + " output " + savePath + ".pdf";
                    exec.executeCommand(command);
                    System.out.println(command);
                    command = "pdftk " + savePath + "TEMP.pdf background " + savePathWatermark + " output " + savePath + ".pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                    File del = new File(savePath + "TEMP.pdf");
                    del.delete();
                }
                else if (stampSet) {
                    command = "pdftk " + pdflistText() + operation + " " + showCatTasks() + " output " + savePath + "TEMP.pdf";
                    exec.executeCommand(command);
                    System.out.println(command);
                    command = "pdftk " + savePath + "TEMP.pdf multistamp " + savePathWatermark + " output " + savePath + ".pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                    File del = new File(savePath + "TEMP.pdf");
                    del.delete();
                }
                else {
                    command = "pdftk " + pdflistText() + operation + " " + showCatTasks() + " output " + savePath + ".pdf" + putPasswordIn();
                    output = exec.executeCommand(command);
                }
            }
            System.out.println(command);
            System.out.println(output);
            if (output.equals("")) {
                correctInput.setText("Creation successful");
            }
            else {
                correctInput.setText(output);
            }
        }
        else {
            correctInput.setText("Parameters not selected or files not selected!");
        }
        savePath = null;

    }

    private String pdflistText() {
        StringBuilder comp = new StringBuilder();
        for (File item : pdflist) {
            comp.append(calculateLetters(pdflist.indexOf(item)) + "=" + item.getAbsolutePath() + " ");
        }
        return comp.toString();
    }

    //Adds a pdf to the list of available pdfs
    public void addPDF(ActionEvent actionEvent) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        try {
            List<File> temp = fileChooser.showOpenMultipleDialog(null);
            for (File each : temp) {
                fileChooser.setInitialDirectory(each.getParentFile());
                pdflist.add(each);
                pdfs.add(each.getName());
                mylist.setItems(pdfs);
                hashKeys.add(each.getAbsolutePath());
                tasks.put(each.getAbsolutePath(), new ArrayList<String>());
            }
            // showLines();
        }
        catch (Exception e) {

        }

    }

    //Removes all available pdfs and deletes all tasks associated
    public void removeAll(ActionEvent actionEvent) {
        pdflist.clear();
        pdfs.clear();
        mylist.setItems(pdfs);
        tasks.clear();
        hashKeys.clear();
        //showLines();
    }

    //Clears only the tasks
    public void removeTasks(ActionEvent actionEvent) {
        tasks.clear();
        correctInput.setText("Removed Tasks!");
    }

    //Removes an individual pdf
    public void removePDF(ActionEvent actionEvent) {
        pdfs.remove(selectedIndex);
        pdflist.remove(selectedIndex);
        tasks.remove(hashKeys.get(selectedIndex));
        hashKeys.remove(selectedIndex);
        System.out.println("pdfs:" + pdfs.size() + "pdflist:" + pdflist.size());
        popup.close();
        referenceToOther.shareInformation(null, pdfs, pdflist, tasks, hashKeys);
    }

    //Closes the popup window
    public void closeWindow(ActionEvent actionEvent) {
        popup.close();
        referenceToOther.shareInformation(null, pdfs, pdflist, tasks, hashKeys);
    }

    public void setStamp(ActionEvent actionEvent) {
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Add Stamp", "*.pdf"));
        try {
            savePathStamp = fileChooser.showOpenDialog(null);
            showStampLoc();
            stampSet = true;
            watermarkSet = false;
            savePathWatermark = null;
            watermarkArea.clear();
            fileChooser.setInitialDirectory(savePathStamp.getParentFile());
        }
        catch (Exception e) {

        }
    }

    public void setFileToDecrypt(ActionEvent actionEvent) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File to decrypt", "*.pdf"));
        try {
            filetoDecrypt = fileChooser.showOpenDialog(null);
            showDecryptLoc();
            fileChooser.setInitialDirectory(filetoDecrypt.getParentFile());
        }
        catch (Exception e) {

        }
    }

    public void saveDecryptLocation(ActionEvent actionEvent) {
        fileChooser.setTitle("Save PDF");
        try {
            decryptSaveFile = fileChooser.showSaveDialog(null);
            int strSize = decryptSaveFile.getAbsolutePath().length();

            if (decryptSaveFile.getAbsolutePath().substring(strSize - 4, strSize).equals(".pdf")) {
                decryptSavePath = decryptSaveFile.getAbsolutePath().substring(0, strSize - 4);
            } else {
                decryptSavePath = decryptSaveFile.getAbsolutePath();
            }
            decryptSavePath += ".pdf";
            showDecryptSaveLoc();
            fileChooser.setInitialDirectory(decryptSaveFile.getParentFile());
        }
        catch (Exception e) {
        }
    }

    public void saveDecryptedFile(ActionEvent actionEvent) {
        ExecuteShellCommand exec = new ExecuteShellCommand();
        String command = "";
        try {
            command = "pdftk " + filetoDecrypt.getAbsolutePath() + " input_pw " + passwordAreaD.getText() + " output " + decryptSavePath;
            if (!filetoDecrypt.getAbsolutePath().equals("") && !passwordAreaD.getText().equals("") && !decryptSavePath.equals("")) {
                exec.executeCommand(command);
                System.out.println(command);
                decryptSaveFile = null;
                filetoDecrypt = null;
                decryptSavePath = "";
                decryptSave.clear();
                fileToDecrypt.clear();
                userPasswordAreaD.clear();
                passwordAreaD.clear();
            }
        }
        catch (Exception e) {
            correctPassDecrypt.setText("Invalid input");
        }
    }

    public void setWatermark(ActionEvent actionEvent) {
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Add Watermark", "*.pdf"));
        try {
            savePathWatermark = fileChooser.showOpenDialog(null);
            showWatermarkLoc();
            stampSet = false;
            watermarkSet = true;
            savePathStamp = null;
            stampArea.clear();
            fileChooser.setInitialDirectory(savePathWatermark.getParentFile());
        }
        catch (Exception e) {

        }
    }

    public void removeStamp(ActionEvent event) {
        savePathStamp = null;
        stampSet = false;
        stampArea.clear();
    }

    public void removeWatermark(ActionEvent event) {
        savePathWatermark = null;
        watermarkSet = false;
        watermarkArea.clear();
    }

    //Creates a unique 3 character string from an integer
    private static String calculateLetters(int index) {
        int lsd = index % 26;
        int ssd;
        int msd;
        double ssdi = index / 26;
        double msdi = ssdi / 26;
        if (ssdi < 0) {
            ssd = 0;
        }
        else {
            ssd = (int) ssdi;
        }
        if (msdi < 0) {
            msd = 0;
        }
        else {
            msd = (int) msdi;
        }
        if (ssd > 25) {
            ssd = ssd % 26;
        }
        char one   = (char) (lsd + 'A');
        char two   = (char) (ssd + 'A');
        char three = (char) (msd + 'A');
        return three + "" + two + "" + one + "";
    }

    //Sets the save location of the new pdf
    public void saveLocation(ActionEvent actionEvent) {
        fileChooser.setTitle("Save PDF");
        try {
            saveLocation = fileChooser.showSaveDialog(null);
            int strSize = saveLocation.getAbsolutePath().length();

            if (saveLocation.getAbsolutePath().substring(strSize - 4, strSize).equals(".pdf")) {
                savePath = saveLocation.getAbsolutePath().substring(0, strSize - 4);
            } else {
                savePath = saveLocation.getAbsolutePath();
            }
            fileChooser.setInitialDirectory(saveLocation.getParentFile());
            create();
        }
        catch (Exception e) {
        }
    }
}
