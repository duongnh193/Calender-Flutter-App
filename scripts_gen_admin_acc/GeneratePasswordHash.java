import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt hash for passwords.
 * Usage: java GeneratePasswordHash.java <password>
 */
public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = args.length > 0 ? args[0] : "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("==========================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("==========================================");
        System.out.println("\nSQL Query:");
        System.out.println("UPDATE app_user SET password_hash = '" + hash + "' WHERE email = 'admin@lichvannien.vn';");
    }
}

