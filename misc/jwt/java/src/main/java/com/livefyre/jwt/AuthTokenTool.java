package com.livefyre.jwt;

public class AuthTokenTool {

    private static final String USAGE = 
            "\nAn example program to generate a livefyre authentication token from parameters passed on the commandline.\n" +
            "\nUsage:\n" +
                    "\tjava ... com.livefyre.jwt.AuthTokenTool <networkId> <networkSecret> <userId> <displayName> <expiry>\n" +
                    "\nParameters:\n" +
                    "\tnetworkId: a string provided by livefyre; e.g. yourco.fyre.co\n" +
                    "\tnetwortSecret: a string provided by livefyre; this is a string of characters\n" +
                    "\tuserId: the unique identifier for the user in your system\n" +
                    "\tdisplayName: the display name of the user; e.g. 'Angry Bob'\n" +
                    "\texpiry: the epoch-time until which the token is valid; e.g. 1324453468\n";
                    
    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println(USAGE);
            System.exit(1);
        }
        
        try {
            String networkName = args[1], 
                    networkSecret = args[2], 
                    userId = args[3], 
                    displayName = args[4];
            double expires = Double.parseDouble(args[5]);

            JWTAuthToken t = new JWTAuthToken(networkName, networkSecret,
                    userId, displayName, expires);

            System.out.println(t.toString());

        } catch (Exception e) {
            System.err.println("There was an exception!!!");
            e.printStackTrace();
        }
    }
}