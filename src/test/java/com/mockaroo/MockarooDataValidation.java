package com.mockaroo;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MockarooDataValidation {

		WebDriver driver;
		static String expectedTitle = "Mockaroo - Random Data Generator and API Mocking Tool | JSON / CSV / SQL / Excel";
		Select select;
		List<String[]> list;
		
		@BeforeClass
		public void setUp() {
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}
		
		@Test(priority = 0)
		public void titleVerification(){
			driver.get("https://mockaroo.com/");
			String actualTitle = driver.getTitle();
			assertEquals(actualTitle, expectedTitle);	
		}
		
		@Test(priority = 1)
		public void logoVerification() {
			WebElement brand = driver.findElement(By.xpath("//div[@class='brand']"));
			WebElement tagLine = driver.findElement(By.xpath("//div[@class='tagline']"));
			assertTrue(brand.isDisplayed(),"Brand not displayed");
			assertTrue(tagLine.isDisplayed(),"Tag line not displayed");
		}
		
		@Test(priority = 2)
		public void removeFields() throws InterruptedException {
			List<WebElement> removeX = driver.findElements(By.xpath("//div[@class='column column-remove']//a"));
			for (int i = 1; i <= removeX.size(); i++) {
				WebElement each = driver.findElement(By.xpath("(//div[@class='column column-remove']//a)["+ i +"]"));
				if(each.isDisplayed()) {
					each.click();
					Thread.sleep(2000);
				}
			}
			//asserting now remove fields are left after removing them all
			assertFalse(driver.findElement(By.xpath("//div[@class='column column-remove']//a")).isDisplayed());
		}
		
		@Test(priority = 3)
		public void assertNameTypeOption() {
			WebElement fieldName = driver.findElement(By.xpath("//div[@class='column column-header column-name']"));
			WebElement type = driver.findElement(By.xpath("//div[@class='column column-header column-type']"));
			WebElement options = driver.findElement(By.xpath("//div[@class='column column-header column-options']"));
			assertTrue(fieldName.isDisplayed());
			assertTrue(type.isDisplayed());
			assertTrue(options.isDisplayed());
		}
		
		@Test(priority = 4)
		public void addAnotherField() {
			WebElement addButton = driver.findElement(By.xpath("//a[.='Add another field']"));
			assertTrue(addButton.isEnabled());
		}
		
		@Test(priority = 5)
		public void rowNumber() {
			WebElement rowNumberField = driver.findElement(By.xpath("//input[@id='num_rows']"));
			int rowNum = Integer.parseInt(rowNumberField.getAttribute("value"));
			assertEquals(rowNum, 1000);
		}
		
		@Test(priority = 6)
		public void formatValue() {
			WebElement formatValue = driver.findElement(By.xpath("//select[@id='schema_file_format']"));
			select = new Select(formatValue);
			String actualDefault = select.getFirstSelectedOption().getText();
			assertEquals(actualDefault, "CSV");
		}
		
		@Test(priority = 7)
		public void lineEnding() {
			WebElement opSys = driver.findElement(By.xpath("//select[@id='schema_line_ending']"));
			select = new Select(opSys);
			String actualDefault = select.getFirstSelectedOption().getText();
			assertEquals(actualDefault,"Unix (LF)");
		}
		
		@Test(priority = 8)
		public void headerAndBom(){
			WebElement header = driver.findElement(By.xpath("//input[@id='schema_include_header']"));
			WebElement BOM = driver.findElement(By.xpath("//input[@id='schema_bom']"));
			assertTrue(header.isSelected());
			assertTrue(!BOM.isSelected());
		}
		
		@Test(priority = 9)
		public void addingAnotherField() throws InterruptedException {
			driver.findElement(By.xpath("//a[.='Add another field']")).click();
			driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[7]")).clear(); 
			driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[7]")).sendKeys("City");
			driver.findElement(By.xpath("(//input[@class='btn btn-default'])[7]")).click();
			
			//asserting Choose a Type window is displayed
			assertTrue(driver.findElement(By.xpath("//div[@id='type_dialog_wrap']")).isDisplayed());
			
			Thread.sleep(2000);
			driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("city");
			Thread.sleep(1000);
			driver.findElement(By.xpath("(//div[@id='type_list']/div)[1]")).click();
			Thread.sleep(2000);
		
			driver.findElement(By.xpath("//a[@class='btn btn-default add-column-btn add_nested_fields']")).click();
			driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[8]")).clear();
			driver.findElement(By.xpath("(//input[@placeholder='enter name...'])[8]")).sendKeys("Country");
			driver.findElement(By.xpath("(//input[@class='btn btn-default'])[8]")).click();
			
			//asserting Choose a Type window is displayed
			assertTrue(driver.findElement(By.xpath("//div[@id='type_dialog_wrap']")).isDisplayed());
			Thread.sleep(2000);
			driver.findElement(By.xpath("//input[@id='type_search_field']")).clear();
			driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("country");
			driver.findElement(By.xpath("(//div[@id='type_list']/div)[1]")).click();
			Thread.sleep(2000);
			driver.findElement(By.xpath("//button[@id='download']")).click();
			Thread.sleep(3000);
		}
		@Test(priority = 10)
		public void readExcelFile() {
			list = new ArrayList<>();
			String filePath = "C:\\Users\\Admin\\Downloads\\MOCK_DATA.csv";
			String line ="";
			String splitBy =",";
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(filePath));
				while( (line=br.readLine()) != null  ) {
					list.add( line.split(splitBy) );
				}
				//asserting first rows are City and Country
				assertTrue(list.get(0)[0].equalsIgnoreCase("city"));
				assertTrue(list.get(0)[1].equalsIgnoreCase("country"));
				assertEquals(list.size(),1001);

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		@Test (priority = 11)
		public void addCityandCountry() {
			List<String> cities = new ArrayList<>();
			List<String> countries = new ArrayList<>();
			
			//storing all City Names in cities ArrayList
			//storing all Country Names in countries ArrayList
			for (int i = 0; i < list.size(); i++) {
				cities.add(list.get(i)[0]);
				countries.add(list.get(i)[1]);
			}
			
			//this will sort items according to length, comes from utilities class
			MockarooUtils m = new MockarooUtils();
			
			Collections.sort(cities, m);	//sorted cities according to length
			Collections.sort(countries, m); //sorted countries according to length


			System.out.println("City with shortest names: " + cities.get(0));
			
			System.out.println("---------finding how many times each country is mentioned----------");
			Set<String> unique = new HashSet<String>(countries);
			for (String key : unique) {
			    System.out.println(key + ": " + Collections.frequency(countries, key));
			}
			
			//Storing unique city names and unique country names in HashMap
			Set<String> uniqueCityNames = new HashSet<>(cities);
			System.out.println("The count of unique cities: " + uniqueCityNames.size());
			Set<String> uniqueCountryNames = new HashSet<>(countries);
			System.out.println("The count of unique countries: " + uniqueCountryNames.size());
			
			assertEquals(uniqueCityNames.size(), 982);
			assertEquals(uniqueCountryNames.size(), 130);
		}
		
		@AfterClass
		public void tearDown() {
			//closing the driver
			driver.close();
		}
	

}
