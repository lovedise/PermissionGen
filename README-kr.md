# PermissionGen ([Eng](https://github.com/lovedise/PermissionGen/blob/master/README.md))

[ ![Download](https://api.bintray.com/packages/lovedise/maven/PermissionGen/images/download.svg) ](https://bintray.com/lovedise/maven/PermissionGen/_latestVersion)

```PermissionGen``` 은 Andorid M 에서 권한 관리를 쉽게 도와주는 라이브러리 입니다.
##설치방법

Maven:

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

##사용법
권한을 사용하는 곳에서 아래 코드를 호출합니다.
requestCode 는 결과를 requestCode가 적용된 어노테이션에 매칭되어 실행 됩니다.

```java
PermissionGen.with(MainActivity.this)
	.addRequestCode(100)
	.permissions(
		Manifest.permission.READ_CONTACTS,
		Manifest.permission.RECEIVE_SMS,
		Manifest.permission.WRITE_CONTACTS)
	.request();
```

또는 아래와 같이도 사용 가능합니다. 편한 방법으로 사용하시면 됩니다.

```java
PermissionGen.needPermission(ContactFragment.this, 100, 
	new String[] {
		Manifest.permission.READ_CONTACTS, 
		Manifest.permission.RECEIVE_SMS,
		Manifest.permission.WRITE_CONTACTS
	}
);
```

위에서 권한을 시도하면 Activity 또는 Fragment 에서 오버라이드한 onRequestPermissionsResult 에서 결과를 받을 수 있습니다.
그럼 아래와 같이 ```PermissionGen.onRequestPermissionsResult```
를 호출해야만 성공, 실패에 대한 어노테이션이 선언된 함수가 실행됩니다.

```java
@Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
	PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
}
```

위에서 호출한 결과로 권한들을 획득했으면 @PermissionSuccess 어노테이션이 선언된 메소드에 requestCode 와 일치하는 함수를 실행합니다.

```java
@PermissionSuccess(requestCode = 100)
public void doSomething(){
	Toast.makeText(this, "Contact permission is granted", Toast.LENGTH_SHORT).show();
}
```

또는 권한이 하나의 퍼미션이라도 거부되었다면 @PermissionFail 어노테이션을 선언된 메소드에 requestCode 와 일치하는 함수를 실행합니다.

```java
@PermissionFail(requestCode = 100)
public void doFailSomething(){
	Toast.makeText(this, "Contact permission is not granted", t.LENGTH_SHORT).show();
}
```

버그, 이슈, 텍스트수정등 어떤내용이라도 피드백 주시면 감사하겠습니다.
이슈나 Pull Request 를 이용주세요~~

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