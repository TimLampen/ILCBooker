
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.github.javafaker.Faker;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

/**
 */
public class MainClass {
    private static String user = "";
    private static String pass = "";











    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    /*
    * assumes you're already logged in
    * */
    public static void bookRoomToday(ChromeDriver driver) throws InterruptedException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        List<Room> rooms = new ArrayList<>();
        rooms.add(Room.BMH321);
        rooms.add(Room.BMH319);
        rooms.add(Room.BMH229);
        rooms.add(Room.BMH228);
        rooms.add(Room.BMH220);
        rooms.add(Room.BMH218);



        HashMap<Room, Integer> bookedRooms = new HashMap<>();
        String formattedDate = format.format(c.getTime());
        if(c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)//skip fri / sat / sun
            return;
        System.out.println("\n\nNow trying to book rooms for " + formattedDate + " (" + new SimpleDateFormat("EEEE").format(c.getTime()) + ")");
        driver.get("https://queensu.evanced.info/dibs/Search");

        //now we in the select page
        Select drpTime = new Select(driver.findElement(By.name("SelectedTime")));
        drpTime.selectByValue("3");

        Select dropPeople = new Select(driver.findElement(By.name("SelectedRoomSize")));
        dropPeople.selectByValue("4,10");

        Select drpSort = new Select(driver.findElement(By.name("SelectedTimeSort")));
        drpSort.selectByValue("Afternoon");

        Select drpDate = new Select(driver.findElement(By.name("SelectedSearchDate")));
        drpDate.selectByValue(formattedDate);

        driver.findElement(By.id("frmSearch")).submit();
        //now in times page

        JavascriptExecutor executor = (JavascriptExecutor)driver;
        for(Time time : Time.values()) {
            String noTimesXPath = "/html/body/div[3]/form/div/div[2]/h3";
            System.out.println("Checking for rooms open at " + time.getFormattedTime());
            if(driver.findElements(By.xpath(noTimesXPath)).size()!=0 && driver.findElement(By.xpath(noTimesXPath)).getText().contains("There are no times available")) {
                System.out.println(ANSI_RED + "No rooms are open at this time (1)." + ANSI_RESET);//check if doesn't exist
                return;
            }

            boolean found = false;
            for(WebElement element : driver.findElements(By.className("title"))){
                if(element.getText().contains(time.getFormattedTime())){
                    executor.executeScript("$('#SelectedStartTime').val('"+ formattedDate + " " + time.getLookupTime() + "');$('#frmTimes').submit();");

                    for (Room room : rooms) {
                        if(bookedRooms.getOrDefault(room, 0) >= 2)
                            return;
                        boolean foundRoom = false;
                        int counter = 1;
                        while(!foundRoom){
                            if(counter==8) {
                                counter++;
                                return;
                            }
                            String roomXPath = "/html/body/div[3]/form/div/div[2]/div[" + counter +"]/div/div[1]/div[2]";
                            if(driver.findElements(By.xpath(roomXPath)).size()==0) {
                                break;
                            }
                            System.out.println(room.getFormattedName() + " comparing against " + driver.findElement(By.xpath(roomXPath)).getText());
                            if(driver.findElement(By.xpath(roomXPath)).getText().contains(room.getFormattedName()))
                                foundRoom = true;
                            counter++;
                        }


                        if(foundRoom) {
                            System.out.println(ANSI_GREEN + "Booking" + ANSI_RESET + " room " + ANSI_YELLOW + room.getFormattedName() + ANSI_RESET + " on " + ANSI_YELLOW + formattedDate + ANSI_RESET + " at " + ANSI_YELLOW + time.getLookupTime() + ANSI_RESET);
                            executor.executeScript("$('#SelectedRoomID').val(" + room.getId() + ");$('#frmRooms').submit();");
                            driver.findElement(By.id("Phone")).sendKeys("3069884048");


                            WebElement submit = driver.findElement(By.id("btnCallDibs"));
                            Actions builder = new Actions(driver);
                            ActionBuilder clickSubmit = builder.moveToElement(submit).click().build();
                            clickSubmit.perform();                                Thread.sleep(5000);

                            if(driver.findElement(By.id("errorModal")).getCssValue("display").contains("block")){
                                if(driver.findElement(By.id("divErrorMsg")).getText().contains("2")){
                                    System.out.println(ANSI_RED + "Unable to book room, you already have 2 rooms booked this day. Continuing anyway.");
                                    break;
                                }
                                else if(driver.findElement(By.id("divErrorMsg")).getText().contains("4")) {
                                    System.out.println(ANSI_RED + "Unable to book room, you have already booked 4 rooms :(");
                                    driver.close();
                                    return;
                                }

                            }

                            found = true;
                            bookedRooms.put(room, bookedRooms.getOrDefault(room, 0)+1);
                            executor.executeScript("window.history.go(-2);");
                            break;
                        }
                    }
                    break;
                }
            }
            if(!found) {
                System.out.println(ANSI_RED + "No rooms are open at this time (2)." + ANSI_RESET);//check if doesn't exist
                executor.executeScript("window.history.go(-1);");
            }


        }

        c.add(Calendar.DATE, 1);
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        System.setProperty("webdriver.chrome.driver","chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        ChromeDriver driver = new ChromeDriver(options);


        driver.get("https://queensu.evanced.info/dibs/Login");

        WebElement usernameElement = driver.findElement(By.id("txtUsername"));
        WebElement passwordElement = driver.findElement(By.id("pwdPassword"));
        WebElement submit = driver.findElement(By.id("btnLoginSubmit"));

        usernameElement.sendKeys(user);
        passwordElement.sendKeys(pass);

        Actions builder = new Actions(driver);

        Action clickSubmit = builder.moveToElement(submit).click().build();
        clickSubmit.perform();

        bookRoomToday(driver);

        driver.close();
    }


}
