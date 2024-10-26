package net.cubecraft.util;

public interface ArgumentDispatcher {
    <T> void getValue(String id, T fallback);

    boolean matchAttribute(String id);




    class PrefixDispatcher implements ArgumentDispatcher {





        @Override
        public <T> void getValue(String id, T fallback) {

        }

        @Override
        public boolean matchAttribute(String id) {
            return false;
        }
    }
}
