import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Made for my keyboard shortcut for toggling Gnome's Hide Clock extention(https://github.com/grantmcwilliams/Gnome_Hide_Clock/tree/master)
 * 
 * @author Mateus Rocha
 */
public class DisableOrEnable{

    InputStreamReader reader;
    OutputStreamWriter writer;
    enum StateEnum{
        ENABLED,DISABLED;
    }

    /**
     * Instantiates reader and writer
     */
    public DisableOrEnable(){
        try {
            reader = new InputStreamReader(new FileInputStream("hide_clock@grantmcwilliams.com/laststate.txt"));
        }catch (IOException e){
            throw new IOError(e);//should not happen
        }
    }

    public static void main(String[] args){
        DisableOrEnable disableOrEnable = new DisableOrEnable();
        String extentionInfo = args[0]; //output from gnome-extentions info
        Pattern statePattern = Pattern.compile("State: ");
        Matcher statePatternMatcher = statePattern.matcher(extentionInfo);
        statePatternMatcher.find();
        boolean enabled = extentionInfo.substring(statePatternMatcher.end()).equals("ENABLED") ;

        /*When the pc starts all extention states are "INITIALIZED", for fixing it i persist them after every toggle(laststate.txt)
        so i can use the persisted state it every time it says "INITIALIZED" */
        if(extentionInfo.substring(statePatternMatcher.end()).equals("INITIALIZED")){
            try{
                StateEnum state = disableOrEnable.getPersistedState();
                if(state == StateEnum.ENABLED){
                    enabled = true;
                }else{
                    enabled = false;
                }
            }catch (IOException e){
                throw new IOError(e);//should not happen
            }
        }
        System.out.print(enabled?"disable":"enable");
        try {
            disableOrEnable.persistState(enabled ? StateEnum.ENABLED : StateEnum.DISABLED);
        }catch (IOException e){
            throw new IOError(e); //Should not happen
        }
    }

    /**
     * Part of the fix for "INITIALIZED" state instead of ENABLED and DISABLED(read main method)
     * @return Persisted state stored on laststate.txt
     */
    StateEnum getPersistedState() throws IOException{
        String stateString = "";
        int lastReaded;
        while(true){
            lastReaded = reader.read();
            if(lastReaded == -1){
                break;
            }
            stateString += (char) lastReaded;
        }
        return StateEnum.valueOf(stateString);
    }

    /**
     * Part of the fix for "INITIALIZED" state instead of ENABLED and DISABLED(read main method)
     */
     void persistState(StateEnum state) throws IOException{
         writer = new OutputStreamWriter(new FileOutputStream("hide_clock@grantmcwilliams.com/laststate.txt"));
         writer.write(state.toString());
         writer.flush();
    }
}