all:
	android update project -p . -t android-23
	ant debug

install:
	adb install -r bin/ITER-debug.apk
	adb shell am start -n app.abhijit.iter/app.abhijit.iter.MainActivity

clean:
	ant clean
	rm local.properties
