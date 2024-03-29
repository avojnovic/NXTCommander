
package android.nxt;

public class LCPMessage {

    public static byte DIRECT_COMMAND_REPLY = 0x00;
    public static byte SYSTEM_COMMAND_REPLY = 0x01;
    public static byte REPLY_COMMAND = 0x02;
    public static byte DIRECT_COMMAND_NOREPLY = (byte)0x80; 
    public static byte SYSTEM_COMMAND_NOREPLY = (byte)0x81; 


    public static final byte START_PROGRAM = 0x00;
    public static final byte STOP_PROGRAM = 0x01;
    public static final byte PLAY_SOUND_FILE = 0x02;
    public static final byte PLAY_TONE = 0x03;
    public static final byte SET_OUTPUT_STATE = 0x04;
    public static final byte SET_INPUT_MODE = 0x05;
    public static final byte GET_OUTPUT_STATE = 0x06;
    public static final byte GET_INPUT_VALUES = 0x07;
    public static final byte RESET_SCALED_INPUT_VALUE = 0x08;
    public static final byte MESSAGE_WRITE = 0x09;
    public static final byte RESET_MOTOR_POSITION = 0x0A;   
    public static final byte GET_BATTERY_LEVEL = 0x0B;
    public static final byte STOP_SOUND_PLAYBACK = 0x0C;
    public static final byte KEEP_ALIVE = 0x0D;
    public static final byte LS_GET_STATUS = 0x0E;
    public static final byte LS_WRITE = 0x0F;
    public static final byte LS_READ = 0x10;
    public static final byte GET_CURRENT_PROGRAM_NAME = 0x11;
    public static final byte MESSAGE_READ = 0x13;
    

    public static byte NXJ_DISCONNECT = 0x20; 
    public static byte NXJ_DEFRAG = 0x21;
    

    public static final byte SAY_TEXT = 0x30;
    public static final byte VIBRATE_PHONE = 0x31;
    public static final byte ACTION_BUTTON = 0x32;
    
    
    public static final byte OPEN_READ = (byte)0x80;
    public static final byte OPEN_WRITE = (byte)0x81;
    public static final byte READ = (byte)0x82;
    public static final byte WRITE = (byte)0x83;
    public static final byte CLOSE = (byte)0x84;
    public static final byte DELETE = (byte)0x85;        
    public static final byte FIND_FIRST = (byte)0x86;
    public static final byte FIND_NEXT = (byte)0x87;
    public static final byte GET_FIRMWARE_VERSION = (byte)0x88;
    public static final byte OPEN_WRITE_LINEAR = (byte)0x89;
    public static final byte OPEN_READ_LINEAR = (byte)0x8A;
    public static final byte OPEN_WRITE_DATA = (byte)0x8B;
    public static final byte OPEN_APPEND_DATA = (byte)0x8C;
    public static final byte BOOT = (byte)0x97;
    public static final byte SET_BRICK_NAME = (byte)0x98;
    public static final byte GET_DEVICE_INFO = (byte)0x9B;
    public static final byte DELETE_USER_FLASH = (byte)0xA0;
    public static final byte POLL_LENGTH = (byte)0xA1;
    public static final byte POLL = (byte)0xA2;
    
    public static final byte NXJ_FIND_FIRST = (byte)0xB6;
    public static final byte NXJ_FIND_NEXT = (byte)0xB7;
    public static final byte NXJ_PACKET_MODE = (byte)0xff;
    
    
    public static final byte MAILBOX_EMPTY = (byte)0x40;
    public static final byte FILE_NOT_FOUND = (byte)0x86;
    public static final byte INSUFFICIENT_MEMORY = (byte) 0xFB;
    public static final byte DIRECTORY_FULL = (byte) 0xFC;
    public static final byte UNDEFINED_ERROR = (byte) 0x8A;
    public static final byte NOT_IMPLEMENTED = (byte) 0xFD;

    
    public static byte[] FIRMWARE_VERSION_LEJOSMINDDROID = { 0x6c, 0x4d, 0x49, 0x64 };

    public static byte[] getBeepMessage(int frequency, int duration) {
        byte[] message = new byte[6];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = PLAY_TONE;
    
        message[2] = (byte) frequency;
        message[3] = (byte) (frequency >> 8);
    
        message[4] = (byte) duration;
        message[5] = (byte) (duration >> 8);

        return message;
    }
    
    public static byte[] getActionMessage(int actionNr) {
        byte[] message = new byte[3];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = ACTION_BUTTON;
        message[2] = (byte) actionNr;
        return message;
    }   

    public static byte[] getMotorMessage(int motor, int speed) {
        byte[] message = new byte[12];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = SET_OUTPUT_STATE;
    
        message[2] = (byte) motor;

        if (speed == 0) {
            message[3] = 0;
            message[4] = 0;
            message[5] = 0;
            message[6] = 0;
            message[7] = 0;

        } else {
    
            message[3] = (byte) speed;
    
            message[4] = 0x03;
    
            message[5] = 0x01;
    
            message[6] = 0x00;
    
            message[7] = 0x20;
        }

    
        message[8] = 0;
        message[9] = 0;
        message[10] = 0;
        message[11] = 0;

        return message;

    }

    public static byte[] getMotorMessage(int motor, int speed, int end) {
        byte[] message = getMotorMessage(motor, speed);

    
        message[8] = (byte) end;
        message[9] = (byte) (end >> 8);
        message[10] = (byte) (end >> 16);
        message[11] = (byte) (end >> 24);

        return message;
    }

    public static byte[] getResetMessage(int motor) {
        byte[] message = new byte[4];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = RESET_MOTOR_POSITION;
    
        message[2] = (byte) motor;
    
        message[3] = 0;

        return message;
    }

    public static byte[] getStartProgramMessage(String programName) {
        byte[] message = new byte[22];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = START_PROGRAM;

    
        for (int pos=0; pos<programName.length(); pos++)
            message[2+pos] = (byte) programName.charAt(pos);

        message[programName.length()+2] = 0;

        return message;
    }

    public static byte[] getStopProgramMessage() {
        byte[] message = new byte[2];

        message[0] = DIRECT_COMMAND_NOREPLY;
        message[1] = STOP_PROGRAM;

        return message;
    }
    
    public static byte[] getProgramNameMessage() {
        byte[] message = new byte[2];

        message[0] = DIRECT_COMMAND_REPLY;
        message[1] = GET_CURRENT_PROGRAM_NAME;

        return message;
    }

    public static byte[] getOutputStateMessage(int motor) {
        byte[] message = new byte[3];

        message[0] = DIRECT_COMMAND_REPLY;
        message[1] = GET_OUTPUT_STATE;
    
        message[2] = (byte) motor;

        return message;
    }

    public static byte[] getFirmwareVersionMessage() {
        byte[] message = new byte[2];

        message[0] = SYSTEM_COMMAND_REPLY;
        message[1] = GET_FIRMWARE_VERSION;

        return message;
    }

    public static byte[] getFindFilesMessage(boolean findFirst, int handle, String searchString) {
        byte[] message;

        if (findFirst)
            message = new byte[22];

        else
            message = new byte[3];

        message[0] = SYSTEM_COMMAND_REPLY;

        if (findFirst) {
            message[1] = FIND_FIRST;

    
            for (int pos=0; pos<searchString.length(); pos++)
                message[2+pos] = (byte) searchString.charAt(pos);

            message[searchString.length()+2] = 0;

        } else {
            message[1] = FIND_NEXT;
            message[2] = (byte) handle;
        }

        return message;
    }

    public static byte[] getOpenWriteMessage(String fileName, int fileLength) {
        byte[] message = new byte[26];

        message[0] = SYSTEM_COMMAND_REPLY;
        message[1] = OPEN_WRITE;
        
    
        for (int pos=0; pos<fileName.length(); pos++)
            message[2+pos] = (byte) fileName.charAt(pos);

        message[fileName.length()+2] = 0;
    
        message[22] = (byte) fileLength;
        message[23] = (byte) (fileLength >>> 8);
        message[24] = (byte) (fileLength >>> 16);
        message[25] = (byte) (fileLength >>> 24);        
        return message;
    }

    public static byte[] getWriteMessage(int handle, byte[] data, int dataLength) {
        byte[] message = new byte[dataLength + 3];

        message[0] = SYSTEM_COMMAND_REPLY;
        message[1] = WRITE;
        
    
        message[2] = (byte) handle;
    
        System.arraycopy(data, 0, message, 3, dataLength);

        return message;
    }
    
    public static byte[] getCloseMessage(int handle) {
        byte[] message = new byte[3];

        message[0] = SYSTEM_COMMAND_REPLY;
        message[1] = CLOSE;
        
    
        message[2] = (byte) handle;

        return message;
    }
    
}
