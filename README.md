## Telegram UI Tests

This fork was created to implement some UI-testcases as a proof-of-concept.
The testcases are based on UIAutomator.

### External Dependencies

The testcases depends on some external tools to communicate with the telegram service for triggering actions and verifying results. 

#### Telegram CLI

Install the [Telegram CLI](https://github.com/rnhmjoj/tg). 
On OSX you might need to perform these commands before building (instead of whats listed in the README there): 

```
brew update
brew upgrade
brew install libconfig readline lua python libevent jansson
export CFLAGS="-I/usr/local/include -I/usr/local/Cellar/readline/6.3.8/include"
export CPPFLAGS="-I/usr/local/opt/openssl/include"
export LDFLAGS="-L/usr/local/opt/openssl/lib -L/usr/local/lib -L/usr/local/Cellar/readline/6.3.8/lib"
./configure && make
```

Start up the Telegram CLI at least once and setup your account. You should use the same phone number that you will use in your testcases so that the confirmation code is sent to your CLI client when requested from the Testcase. 

Create a new contact with your own number (This can only be done in the CLI and is necessary for the testcases to work properly, the testcases will send messages to yourself, otherwise you would need a second CLI user).

* In the telegram CLI: `add_contact <phone> <first name> <last name>`

#### Telegram UI Test Server

The testcases need to trigger actions from telegram (e.g. sending a message) or read messages (reading the verification code or verifying that a message was sent correctly).

Clone [the repo](https://github.com/iv-mexx/TelegramUITestRequestServer) and read the instructions to setup the Telegram UI Test Server.

### Caveats

* The application and simulator are not resetted after the testcases. In order to trigger the registration you need to delete the application or reset the simulator manually between test-runs.