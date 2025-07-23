import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI(){
        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450,650);
        setLocationRelativeTo(null);// Center the window
        setLayout(null); // Use absolute positioning

        setResizable(false);
        addGuiComponents();// Add all UI components
    }

    // Adds and arranges all the GUI components
    private void addGuiComponents(){
        JTextField searchTextField=new JTextField();

        searchTextField.setBounds(15,15,351,45);

        searchTextField.setFont(new Font("Dialog",Font.PLAIN,24));

        add(searchTextField);

        // Weather condition image (default: cloudy)
        JLabel weatherConditionImage=new JLabel(loadImage("src/assets/cloudy.png"));

        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        // Temperature display
        JLabel temperatureText=new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Diolog",Font.BOLD,48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Weather condition description (default: Cloudy)
        JLabel weatherConditionDesc=new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // Humidity icon and label
        JLabel humidityImage=new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        JLabel humidityText=new JLabel("<html><b>Humidity</b> 100% </html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityText);

        // Windspeed icon and label
        JLabel windspeedImage=new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        JLabel windspeedText=new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN,16));
        add(windspeedText);

        // Search button with icon
        JButton searchButton=new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);

        // Action when search button is clicked
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String userInput=searchTextField.getText();

                // Ignore empty input
                if(userInput.replaceAll("\\s","").length()<=0){
                    return;
                }
                
                weatherData=WeatherApp.getWeatherData(userInput);
                if (weatherData == null) {
                    // Fetch weather data for the entered location
                    JOptionPane.showMessageDialog(null,
                            "Place not found. Please enter a valid city name.",
                            "Location Error",
                            JOptionPane.ERROR_MESSAGE);
                    return; // exit early
                }

                // Set weather condition image based on result
                String weatherCondition=(String) weatherData.get("weather_condition");

                switch (weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }
                // Update temperature, condition, humidity, and windspeed labels
                double temperature=(double) weatherData.get("temperature");
                temperatureText.setText((temperature+"C"));
                weatherConditionDesc.setText(weatherCondition);

                long humidity=(long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> "+ humidity +"%</html>");

                double windspeed=(double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> "+ windspeed +" km/h</html>");
            }
        });
        add(searchButton);
    }

    // Loads an image from the given path and returns it as an ImageIcon
    private  ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image= ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }
}
