// var capture;
// var stopLive = false;
// var btnTakePic = document.getElementById("btn-take-photo");
// var btnLiveCam = document.getElementById("btn-live-camera");
// var btnEmotify = document.getElementById("btn-emotify");
// var emotionField = document.getElementById("emotion");
// var button;
// var SERVERURL = "https://facemotify-flaskserver.herokuapp.com";
// var imgBase64;
// var tookPicture = false;
// function setup() {
//     var canvas = createCanvas(640, 480);
//     canvas.parent("canvasParent");
//     background(0);
//     capture = createCapture(VIDEO);
//     print(capture.list);
//     capture.size(640, 480);
//     capture.hide();
//     btnTakePic.addEventListener("click", () => {
//         tookPicture = true;
//         let img = get();
//         // console.log(img);
//         img.loadPixels();
//         // console.log(img.canvas.toDataURL());
//         imgBase64 = img.canvas.toDataURL();
//         // console.log(imgBase64);
//         // save(img, "my.jpg");
//         // console.log(capture);
//         stopLive = true;
//         noLoop();
//     });
//     btnLiveCam.addEventListener("click", () => {
//         tookPicture = false;
//         emotionField.textContent = "";
//         stopLive = false;
//         loop();
//     });
// }

// function draw() {
//     print("Camera is live!");
//     //   let img = createImage(640, 480);
//     //   save(img, "my.png");
//     if (!stopLive) {
//         translate(640, 0);
//         scale(-1, 1);
//         image(capture, 0, 0);
//     }
// }
// btnEmotify.addEventListener("click", () => {
//     // console.log("Reached ere too");
//     if (tookPicture) {
//         $.ajax({
//             type: "POST",
//             url: SERVERURL + "/emotify",
//             data: {
//                 imageBase64: imgBase64,
//             },
//             error: function (data) {
//                 console.log("upload error", data);
//                 console.log(data.getAllResponseHeaders());
//             },
//             success: function (data) {
//                 // alert("hello"); // if it's failing on actual server check server FIREWALL + SET UP CORS
//                 let status = data["status"];
//                 let emotion = data["emotion"];
//                 print("Status: " + status);
//                 print("Emotion: " + emotion);
//                 emotionField.textContent = emotion;
//             },
//         }).done(function () {
//             console.log("finished!");
//             tookPicture = false;
//         });
//     } else {
//         alert(
//             "Error!..Picture not taken\nTake a picture using 'Take Picture' Button first!"
//         );
//     }

//     // Posting using fetch api
//     // fetch(SERVERURL + "/denode/", {
//     //     method: "POST",
//     //     body: JSON.stringify({
//     //         ok: true,
//     //         imgb64: imgBase64,
//     //     }),
//     //     headers: {
//     //         "Content-type": "application/json; charset=UTF-8",
//     //         // "Access-Control-Allow-Origin": origin,
//     //         // "Access-Control-Allow-Credentials": "true",
//     //     },
//     // })
//     //     .then((response) => response.json())
//     //     .then((data) => {
//     //         console.log(data);
//     //     });

//     //Posting using ajax
//     // let formData = new FormData();
//     // formData.append("image", imgBase64);
//     // $.ajax({
//     //     type: "POST",
//     //     url: SERVERURL + "/emotify", // fix this to your liking
//     //     data: formData,
//     //     // cache: false,
//     //     // processData: false,
//     //     // contentType: false,
//     //     error: function (data) {
//     //         console.log("upload error", data);
//     //         console.log(data.getAllResponseHeaders());
//     //     },
//     //     success: function (data) {
//     //         // alert("hello"); // if it's failing on actual server check your server FIREWALL + SET UP CORS
//     //         bytestring = data["status"];
//     //         image = bytestring.split("'")[1];
//     //         print(image);
//     //         // imagebox.attr("src", "data:image/jpeg;base64," + image);
//     //     },
//     // });
// });
