package se233.labadvancepro.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.labadvancepro.Launcher;
import se233.labadvancepro.controller.AllCustomHandler;
import se233.labadvancepro.model.character.BasedCharacter;

public class CharacterPane extends ScrollPane {
    private BasedCharacter character;

    private Label nameLabel, typeLabel, hpLabel, atkLabel, defLabel, resLabel;
    private ImageView mainImage;
    private Button genCharacterButton; //
    private VBox characterInfoPane;

    public CharacterPane() {
        nameLabel = new Label("Name: ");
        typeLabel = new Label("Type: ");
        hpLabel = new Label("HP: ");
        atkLabel = new Label("ATK: ");
        defLabel = new Label("DEF: ");
        resLabel = new Label("RES: ");
        mainImage = new ImageView();

        double CHAR_IMAGE_SIZE = 150;
        mainImage.setFitWidth(CHAR_IMAGE_SIZE);
        mainImage.setFitHeight(CHAR_IMAGE_SIZE);
        mainImage.setPreserveRatio(true);

        genCharacterButton = new Button("Generate Character");

        genCharacterButton.setOnAction(new AllCustomHandler.GenCharacterHandler());

        characterInfoPane = new VBox(10);
        characterInfoPane.setBorder(null);
        characterInfoPane.setPadding(new Insets(25, 25, 25, 25));


        characterInfoPane.getChildren().addAll(nameLabel, mainImage, typeLabel, hpLabel, atkLabel, defLabel, resLabel, genCharacterButton);

        this.setContent(characterInfoPane);
        this.setStyle("-fx-background-color:Red;");
    }
    private Pane getDetailsPane() {
        Pane characterInfoPane = new VBox(10);
        characterInfoPane.setBorder(null);
        characterInfoPane.setPadding(new Insets(25, 25, 25, 25));
        Label name,type,hp,atk,def,res;
        ImageView mainImage = new ImageView();
        if (this.character != null) {
            name = new Label("Name: "+character.getName());
            mainImage.setImage(new Image(Launcher.class.getResource(character.getImagepath()).toString()));
            hp = new Label("HP: "+character.getHp().toString()+"/"+character.getFullHp().toString());
            type = new Label("Type: "+character.getDamageType().toString());
            atk = new Label("ATK: "+character.getPower());
            def = new Label("DEF: "+character.getDefense());
            res = new Label("RES: "+character.getResistance());
        } else {
            name = new Label("Name: ");
            mainImage.setImage(new Image(Launcher.class.getResource("assets/unknown.png").toString()));
            hp = new Label("HP: ");
            type = new Label("Type: ");
            atk = new Label("ATK: ");
            def = new Label("DEF: ");
            res = new Label("RES: ");
        }
        Button genCharacter = new Button();
        genCharacter.setText("Generate Character");
        genCharacter.setOnAction(new AllCustomHandler.GenCharacterHandler());
        characterInfoPane.getChildren().addAll(name,mainImage,type,hp,atk,def,res, genCharacter);
        return characterInfoPane;
    }
    public void drawPane(BasedCharacter character) {
        this.character = character;
        if (this.character != null) {
            nameLabel.setText("Name: " + character.getName());
            mainImage.setImage(new Image(Launcher.class.getResource(character.getImagepath()).toString()));
            hpLabel.setText("HP: " + character.getHp().toString() + "/" + character.getFullHp().toString());
            typeLabel.setText("Type: " + character.getClass().getSimpleName()); // ใช้ getClass().getSimpleName() เหมือนใน EquipPane
            atkLabel.setText("ATK: " + character.getPower());
            defLabel.setText("DEF: " + character.getDefense());
            resLabel.setText("RES: " + character.getResistance());
        } else {
            // กรณีไม่มีตัวละคร (อาจไม่จำเป็นถ้ามีการสร้างตัวละครเริ่มต้นเสมอ)
            nameLabel.setText("Name: ");
            mainImage.setImage(new Image(Launcher.class.getResource("assets/unknown.png").toString()));
            hpLabel.setText("HP: ");
            typeLabel.setText("Type: ");
            atkLabel.setText("ATK: ");
            defLabel.setText("DEF: ");
            resLabel.setText("RES: ");
        }
    }
}