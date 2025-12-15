public class Main {


    interface Notifier {
        void send(String to, String message);

        // default-метод
        default String format(String message) {
            return "[NOTIFY] " + message;
        }
    }


    static abstract class NotificationChannel {
        protected final Notifier notifier;

        protected NotificationChannel(Notifier notifier) {
            this.notifier = notifier;
        }

        public abstract String name();
        public abstract void notifyUser(String to, String message);

        protected void deliver(String to, String message) {
            notifier.send(to, message);
        }
    }


    static class EmailChannel extends NotificationChannel {
        EmailChannel(Notifier notifier) {
            super(notifier);
        }

        @Override
        public String name() {
            return "EMAIL";
        }

        @Override
        public void notifyUser(String to, String message) {
            deliver(to, notifier.format("Email → " + to + ": " + message));
        }
    }

    static class SmsChannel extends NotificationChannel {
        SmsChannel(Notifier notifier) {
            super(notifier);
        }

        @Override
        public String name() {
            return "SMS";
        }

        @Override
        public void notifyUser(String to, String message) {
            deliver(to, notifier.format("SMS → " + to + ": " + message));
        }
    }

    static class PushChannel extends NotificationChannel {
        PushChannel(Notifier notifier) {
            super(notifier);
        }

        @Override
        public String name() {
            return "PUSH";
        }

        @Override
        public void notifyUser(String to, String message) {
            deliver(to, notifier.format("Push → " + to + ": " + message));
        }
    }


    static class SmsNotifier implements Notifier {
        @Override
        public void send(String to, String message) {
            System.out.println("SEND: " + message);
        }


        @Override
        public String format(String message) {
            return "[SMS] " + message;
        }
    }

    static class DefaultNotifier implements Notifier {
        @Override
        public void send(String to, String message) {
            System.out.println("SEND: " + message);
        }
    }


    enum ChannelType {
        EMAIL, SMS, PUSH
    }

    static class ChannelFactory {
        static NotificationChannel create(ChannelType type) {
            if (type == null) {
                throw new IllegalArgumentException("ChannelType is null");
            }

            return switch (type) {
                case EMAIL -> new EmailChannel(new DefaultNotifier());
                case PUSH  -> new PushChannel(new DefaultNotifier());
                case SMS   -> new SmsChannel(new SmsNotifier());
            };
        }
    }


    static void runTests() {

        NotificationChannel email = ChannelFactory.create(ChannelType.EMAIL);
        assert email instanceof EmailChannel;

        NotificationChannel sms = ChannelFactory.create(ChannelType.SMS);
        assert sms instanceof SmsChannel;


        List<NotificationChannel> list = List.of(
                email,
                sms,
                ChannelFactory.create(ChannelType.PUSH)
        );

        assert list.size() == 3;


        String defaultFormatted =
                new DefaultNotifier().format("test");
        String smsFormatted =
                new SmsNotifier().format("test");

        assert defaultFormatted.startsWith("[NOTIFY]");
        assert smsFormatted.startsWith("[SMS]");
    }


    public static void main(String[] args) {
        runTests();

        List<NotificationChannel> channels = List.of(
                ChannelFactory.create(ChannelType.EMAIL),
                ChannelFactory.create(ChannelType.SMS),
                ChannelFactory.create(ChannelType.PUSH)
        );

       
        for (NotificationChannel ch : channels) {
            ch.notifyUser("user@example.com", "Hello!");
        }

        System.out.println("\n✔ Всі вимоги були викнонані одним кодом");
    }
}
