import javax.swing.*;

public class AppLauncher {
    public  static void main(String[] args){
        // Schedule the GUI creation on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                // Create and show the main WeatherApp GUI window
                new WeatherAppGUI().setVisible(true);
//                System.out.println(WeatherApp.getLocationData("Tokyo"));

//                System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}
