// Java Package libcats is a proxy for talking to a Go program.
//   gobind -lang=java github.com/ChrisSmith/libcats
//
// File is generated by gobind. Do not edit.
package go.libcats;

import go.Seq;

public abstract class Libcats {
    private Libcats() {} // uninstantiable
    
    public static final class CallbackToken implements go.Seq.Object {
        private static final String DESCRIPTOR = "go.libcats.CallbackToken";
        private static final int CALL_Close = 0x00c;
        
        private go.Seq.Ref ref;
        
        private CallbackToken(go.Seq.Ref ref) { this.ref = ref; }
        
        public go.Seq.Ref ref() { return ref; }
        
        public void call(int code, go.Seq in, go.Seq out) {
            throw new RuntimeException("internal error: cycle: cannot call concrete proxy");
        }
        
        
        public void Close() {
            go.Seq _in = new go.Seq();
            go.Seq _out = new go.Seq();
            _in.writeRef(ref);
            Seq.send(DESCRIPTOR, CALL_Close, _in, _out);
        }
        
        @Override public boolean equals(Object o) {
            if (o == null || !(o instanceof CallbackToken)) {
                return false;
            }
            CallbackToken that = (CallbackToken)o;
            return true;
        }
        
        @Override public int hashCode() {
            return java.util.Arrays.hashCode(new Object[] {});
        }
        
        @Override public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("CallbackToken").append("{");
            return b.append("}").toString();
        }
        
    }
    
    public static CallbackToken CreateImageCallback(ImageCallback callback) {
        go.Seq _in = new go.Seq();
        go.Seq _out = new go.Seq();
        CallbackToken _result;
        _in.writeRef(callback.ref());
        Seq.send(DESCRIPTOR, CALL_CreateImageCallback, _in, _out);
        _result = new CallbackToken(_out.readRef());
        return _result;
    }
    
    public static byte[] DownloadBytes(String url) throws Exception {
        go.Seq _in = new go.Seq();
        go.Seq _out = new go.Seq();
        byte[] _result;
        _in.writeUTF16(url);
        Seq.send(DESCRIPTOR, CALL_DownloadBytes, _in, _out);
        _result = _out.readByteArray();
        String _err = _out.readUTF16();
        if (_err != null) {
            throw new Exception(_err);
        }
        return _result;
    }
    
    public interface ImageCallback extends go.Seq.Object {
        public void ImageReceived(byte[] image);
        
        public static abstract class Stub implements ImageCallback {
            static final String DESCRIPTOR = "go.libcats.ImageCallback";
            
            private final go.Seq.Ref ref;
            public Stub() {
                ref = go.Seq.createRef(this);
            }
            
            public go.Seq.Ref ref() { return ref; }
            
            public void call(int code, go.Seq in, go.Seq out) {
                switch (code) {
                case Proxy.CALL_ImageReceived: {
                    byte[] param_image = in.readByteArray();
                    this.ImageReceived(param_image);
                    return;
                }
                default:
                    throw new RuntimeException("unknown code: "+ code);
                }
            }
        }
        
        static final class Proxy implements ImageCallback {
            static final String DESCRIPTOR = Stub.DESCRIPTOR;
        
            private go.Seq.Ref ref;
        
            Proxy(go.Seq.Ref ref) { this.ref = ref; }
        
            public go.Seq.Ref ref() { return ref; }
        
            public void call(int code, go.Seq in, go.Seq out) {
                throw new RuntimeException("cycle: cannot call proxy");
            }
        
            public void ImageReceived(byte[] image) {
                go.Seq _in = new go.Seq();
                go.Seq _out = new go.Seq();
                _in.writeRef(ref);
                _in.writeByteArray(image);
                Seq.send(DESCRIPTOR, CALL_ImageReceived, _in, _out);
            }
            
            static final int CALL_ImageReceived = 0x10a;
        }
    }
    
    public static void Init(String cachePath) {
        go.Seq _in = new go.Seq();
        go.Seq _out = new go.Seq();
        _in.writeUTF16(cachePath);
        Seq.send(DESCRIPTOR, CALL_Init, _in, _out);
    }
    
    public static void OnStart() {
        go.Seq _in = new go.Seq();
        go.Seq _out = new go.Seq();
        Seq.send(DESCRIPTOR, CALL_OnStart, _in, _out);
    }
    
    public static void OnStop() {
        go.Seq _in = new go.Seq();
        go.Seq _out = new go.Seq();
        Seq.send(DESCRIPTOR, CALL_OnStop, _in, _out);
    }
    
    private static final int CALL_CreateImageCallback = 1;
    private static final int CALL_DownloadBytes = 2;
    private static final int CALL_Init = 3;
    private static final int CALL_OnStart = 4;
    private static final int CALL_OnStop = 5;
    private static final String DESCRIPTOR = "libcats";
}
