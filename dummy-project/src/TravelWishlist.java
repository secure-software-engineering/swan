import com.travelguru.comm.EmailClient;
import com.travelguru.data.Customer;
import com.travelguru.data.TravelOffer;
import com.travelguru.util.UserManagement;

public class TravelWishlist {

    public static void main(String[] args) {

        //create new customer
        Customer customer = new Customer("Daniel Bruns", "1454876798761237",  3456.89);

        //set password and email
        customer.setPassword("password");
        customer.setEmail("daniel.bruns@gmail.com");

        //send confirmation email
        EmailClient confirmationEmail = new EmailClient();

        String []addresses = new String[]{customer.getEmail(), "user@verification@travelwishlist.com"};
        String subject = "Account Confirmation";
        String emailBody = "Hello " + customer.getName() + ", \nPlease confirm your email and credit card details. " + UserManagement.maskCreditCard(customer.getCreditCard());

        if(confirmationEmail.sendEmail(addresses, subject, emailBody))
            System.out.println("Account verification e-mail was successfully sent.");
        else
            System.out.println("An error occured.");

        TravelOffer offer = new TravelOffer();
    }

}