# PermissionGen ([한글](https://github.com/lovedise/PermissionGen/blob/master/README-kr.md))

[ ![Download](https://api.bintray.com/packages/lovedise/maven/PermissionGen/images/download.svg) ](https://bintray.com/lovedise/maven/PermissionGen/_latestVersion)

```PermissionGen``` can easily handle the permissions in the Android M.

##Download

Grab via Maven:

```Maven
<dependency>
        <groupId>com.lovedise</groupId>
        <artifactId>permissiongen</artifactId>
        <version>0.0.6</version>
</dependency>
```

or Gradle:

```Gradle
compile 'com.lovedise:permissiongen:0.0.6'
```

##Usage
When you request permissions

```java
PermissionGen.with(MainActivity.this)
	.addRequestCode(100)
	.permissions(
		Manifest.permission.READ_CONTACTS,
		Manifest.permission.RECEIVE_SMS,
		Manifest.permission.WRITE_CONTACTS)
	.request();
```

or

```java
PermissionGen.needPermission(ContactFragment.this, 100, 
	new String[] {
		Manifest.permission.READ_CONTACTS, 
		Manifest.permission.RECEIVE_SMS,
		Manifest.permission.WRITE_CONTACTS
	}
);
```

Override the onRequestPermissionsResult in activity or fragment and input this code.

```java
@Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
	PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
}
```


When it succeeded in obtaining permission

```java
@PermissionSuccess(requestCode = 100)
public void doSomething(){
	Toast.makeText(this, "Contact permission is granted", Toast.LENGTH_SHORT).show();
}
```

When it failed in obtaining permission

```java
@PermissionFail(requestCode = 100)
public void doFailSomething(){
	Toast.makeText(this, "Contact permission is not granted", t.LENGTH_SHORT).show();
}
```

##License
```
Copyright 2015 Seunghwan Kim

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
