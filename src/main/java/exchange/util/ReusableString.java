package exchange.util;

public class ReusableString implements Comparable<ReusableString> {
    private char[] chars;
    private int len;
    private int hash = 0;

    public ReusableString(int len) {
        this.len = len;
        chars = new char[len];
    }

    public void copyFromString(String s) {
        if (s.length() > len) {
            chars = new char[s.length()];
        }
        len = s.length();
        copy(chars, s);

    }

    private void copy(char[] chars, String s) {
        for (int i = 0; i < s.length(); i++) {
            chars[i] = s.charAt(i);
        }
    }

    public void reset() {
        len = 0;
        hash = 0;
    }

    @Override
    public int hashCode() {
       if( hash == 0 ){
           int h = 0;
           for( int i = 0 ; i < len ; i++ ){
               h = 31 * h + chars[i];
           }
           hash = h;
       }
       return hash;
    }

    @Override
    public boolean equals( Object obj) {
        if(  obj instanceof ReusableString  ) {
            return equals((ReusableString) obj);
        }
        return false;
    }


    public boolean equals(ReusableString s) {
        if( this == s ){
            return true;
        }
        if( s== null || s.len != len) {
            return false;
        }
        for( int i = 0 ; i < len ; i++ ){
            if( chars[i] != s.chars[i] ){
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(ReusableString o) {
        int min = Math.min( len , o.len );
        int i = 0;
        while ( i < min ){
            if( chars[i] != o.chars[i]){
                return chars[i] - o.chars[i];
            }
            i++;
        }
        return len - o.len;
    }


}
