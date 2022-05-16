/*
** @author philippe.queinnec@enseeiht.fr
** Inspired by IBM TSpaces exemples.
**
**/

package linda.whiteboard;

public class SecureWhiteboard {

    /*** main **
     ** Run the whiteboard as an application.
     **
     ** @param args - command line arguments
     */
    public static void main(String args[]) {
        if (args.length<1){
            System.err.println("Whiteboard [url]");
            System.exit(1);
        }
        WhiteboardModel model = new WhiteboardModel();
        WhiteboardView view = new WhiteboardView(model);
        model.setView(view);
        model.start(new linda.secure.LindaSafeClient(args));
    }
}

