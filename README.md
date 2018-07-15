# Get Started
1. Your Android Device connect to pc with ADB.
2. Enable Proxy.
    ```bash
    $ adb reverse tcp:5000 tcp:5000
    ```
3. Download Server.
    ```bash
    $ git clone https://github.com/prprhyt/python-fido2.git
    $ cd python-fido2
    $ git checkout -b fix-keep-session-in-examples origin/fix-keep-session-in-examples
    ```
4. RUN Server
    ```bash
    $ cd examples/server
    $ python3 server.py
    ```
5. RUN Sample App
