//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.gb2022.commons.container;

import java.util.HashMap;

public class StartArguments {
    public final HashMap<String, String> dispatchedArgs = new HashMap();

    public StartArguments(String[] args) {
        String[] var2 = args;
        int var3 = args.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String arg = var2[var4];
            StringBuilder sb = new StringBuilder();
            String k = null;
            char[] var9 = arg.toCharArray();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                char c = var9[var11];
                if (c == '=') {
                    k = sb.toString();
                    sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }

            String v = sb.toString();
            this.dispatchedArgs.put(k, v);
        }

    }

    public int getValueAsInt(String id, int fallback) {
        return Integer.parseInt((String)this.dispatchedArgs.getOrDefault(id, String.valueOf(fallback)));
    }

    public boolean getValueAsBoolean(String id, boolean fallback) {
        return Boolean.parseBoolean((String)this.dispatchedArgs.getOrDefault(id, String.valueOf(fallback)));
    }

    public String getValueAsString(String id, String fallback) {
        return (String)this.dispatchedArgs.getOrDefault(id, fallback);
    }
}
