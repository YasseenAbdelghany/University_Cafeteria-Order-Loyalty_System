package app.gui.admin;

/**
 * Enumeration of user roles in the Administrative Application.
 * Used for role detection and routing to appropriate dashboards.
 */
public enum UserRole {
    /**
     * Administrator role - full system management access
     */
    ADMIN,
    
    /**
     * Service Manager role - manages specific service areas
     * (Menu, Order, Student, Payment, Report, Notification)
     */
    SERVICE_MANAGER,
    
    /**
     * IT Administrator role - system administration and user management
     */
    IT_ADMIN,
    
    /**
     * Unknown role - authentication failed or role could not be determined
     */
    UNKNOWN
}
