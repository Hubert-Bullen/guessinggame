package be.vdab.guessinggame;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

@Title("Guessing Game")
@Theme("mytheme")
@Widgetset("be.vdab.guessinggame.MyAppWidgetset")
public class MyUI extends UI {

    private int countdown = 3;
    int random = generateRandom();

    VerticalLayout layout = new VerticalLayout();
    TextField input = new TextField();
    Label infoLabel = new Label("");
    Button reset = new Button("Reset the game");
    Button check = new Button("Check your answer");


    @Override
    protected void init(VaadinRequest vaadinRequest) {

        layout.setMargin(true);
        layout.setSpacing(true);

        setContent(layout);

        input.setCaption("Type a number between 1 and 10");
        input.addValidator(new IntegerRangeValidator("Not a number between 1 and 10", 1, 10));
        input.setValidationVisible(false); //Anders is het ook rood van in't begin als het leeg is.
        input.setConverter(new StringToIntegerConverter()); //Convert dan de string naar int voor te kunnen validaten en dergelijke. Erna aanroepen met convertedValue!
        input.setNullRepresentation("");// geen Null meer dan bij lege inputfield

        layout.addComponent(input);

        reset.addClickListener(event -> resetGame()); // Lambda java 8

        check.addClickListener(event -> checkGuess());

        layout.addComponent(check);
        layout.addComponent(infoLabel);
    }

    private int generateRandom(){
        return (int) Math.round(Math.random()*(10-1)+1);
    }

    private void resetGame() {
        infoLabel.setCaption("");
        layout.replaceComponent(reset, check);
        input.setValue("");
        random = generateRandom();
        countdown = 3;
    }

    private void checkGuess() {
        input.setValidationVisible(false);
        try {
            input.validate();
            final int inputValue = (int) input.getConvertedValue();
            if (inputValue == random) {
                infoLabel.setCaption("Correct, the number to guess was indeed: " + random);
                layout.replaceComponent(check, reset);
            } else {
                countdown -= 1;
                if (countdown != 0) {
                    infoLabel.setCaption("Try again! " + countdown + " guesses remaining");
                    input.setValue("");
                } else {
                    infoLabel.setCaption("Sorry no guesses remaining");
                    layout.replaceComponent(check, reset);
                }

            }
        } catch (Validator.InvalidValueException e) {
            input.setValidationVisible(true);
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {}
}
