// ==UserScript==
// @name         TikTok
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  try to take over the world!
// @author       You
// @match        https://seller-vn.tiktok.com/order*
// @icon         data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==
// @grant        none
// ==/UserScript==

(function () {
    'use strict';

    // Your code here...
    window.elems = null;
    window.ajaxCalls = 0; // initial ajax calls
    window.ajaxEndpoints = []; // keeping track of all ajax call urls (endpoints)
    const origOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function () {
        window.ajaxCalls++;
        this.addEventListener('load', (event) => {
            //console.log("hahaahahahahah");
            if (!window.ajaxEndpoints.includes(event['currentTarget'].responseURL)) {
                window.ajaxEndpoints.push(event['currentTarget'].responseURL);
                //console.log(event['currentTarget'].responseURL)
            }
            //console.log("haha -- -- --" + window.ajaxCalls);
            window.ajaxCalls--;
            switch (window.ajaxCalls) {
                case 0: // when ajax calls finish this runs

                    //console.log("hihihihihihihihhihihihihihihhi");
                    //console.log(window.ajaxEndpoints);
                    if (window.elems == null) {
                        var all = document.querySelectorAll(`[class^="index__OrderDeliveryWrapper"]`);
                        //console.log(all);
                        if (all.length != 0) {
                            window.elems = all;
                            for (let i = 0; i < all.length; i++) {

                                let data = all[i].childNodes[1].innerText.split(",");
                                let shipping_provider = data[0];
                                let tracking_number = data[1];

                                switch (shipping_provider) {
                                    case "GHTK":
                                        break;
                                    case "GHN":
                                        fetch('https://fe-online-gateway.ghn.vn/order-tracking/public-api/client/tracking-logs', {
                                            method: 'POST',
                                            headers: {
                                                'Accept': 'application/json',
                                                'Content-Type': 'application/json'
                                            },
                                            body: JSON.stringify({ "order_code": tracking_number })
                                        })
                                            .then(response => response.json())
                                            .then(response => {
                                                // let res = JSON.parse(response);
                                                all[i].appendChild(document.createTextNode(response.data.order_info.status_name));
                                                // console.log(JSON.stringify(response));
                                            })
                                        break;
                                    case "J&T Express":
                                        break;
                                }

                                //all[i].appendChild(document.createTextNode(tracking_number + shipping_provider));

                                // fetch('https://i.ghtk.vn/'+tracking_number)
                                //     .then(res => console.log(res))
                                //     .catch(err => console.log(err));

                                //for (let j = 0; j < all[i].childNodes.length; j++) {
                                //    console.log(all[i].childNodes[j].innerText);
                                //}


                                //for(var child=all[i].firstChild; child!==null; child=child.nextSibling) {
                                //    console.log(child);
                                //}
                                //let child = all[i].chilNodes;
                                //console.log(child);
                                //for (let j = 0; j < child.length; j++) {
                                //    console.log(child[j].innerText);
                                //}
                            }
                            //console.log(window.elems)
                        }

                    }
                    //window.removeEventListener("load", this);
                    break;
            }
        });
        origOpen.apply(this, arguments);
    }



    var y = new MutationObserver(function (e) {
        if (e[0].addedNodes) {
            // console.log('Added!');
            let added = e[0].target.lastChild;
            let type = "";
            let commentator = "";
            let content = "";
            let divContent = added.childNodes[1];
            if (added.getAttribute("data-e2e") === "chat-message") {
                type = "CHAT_MESSAGE";
                commentator = divContent.childNodes[0].childNodes[0].innerText;
                content = divContent.childNodes[1].innerText;
            } else if (added.getAttribute("data-e2e") === "social-message") {
                type = "SOCIAL_MESSAGE";
                commentator = divContent.childNodes[0].innerText;
                content = divContent.childNodes[1].textContent;
            } else return;
            // let body = {"creator": "", "lan": 1, "commentator": commentator, "type": type, "content": content}
            // console.log(body);
            fetch('http://localhost:8080/common/comment/log', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({"creator": "", "lan": 1, "commentator": commentator, "type": type, "content": content})
            })
                .then(response => response.json())
                .then(response => {
                    console.log(JSON.stringify(response));
                })

            GM.xmlHttpRequest({
                method: "POST",
                url: "http://localhost:8080/common/comment/log",
                data: JSON.stringify({"creator": "", "lan": 1, "commentator": commentator, "type": type, "content": content}),
                headers: {
                    "Content-Type": "application/json"
                },
                onload: function(response) {
                    console.log(JSON.stringify(response));
                }
            });

        }
    });
    y.observe(document.getElementsByClassName('tiktok-1c9cfuz-DivChatMessageList ex6o5344')[0], { childList: true })


    document.querySelectorAll("[data-e2e='chat-message']")[0].childNodes[1].childNodes[1].innerText

    var data = document.querySelectorAll("[data-e2e='chat-message']");
    for (let i = 0; i < data.length; i++) {
        let divContent = data[i].childNodes[1];
        let userInfo = divContent.childNodes[0].childNodes[0].innerText;
        let userComment = divContent.childNodes[1].innerText;
        console.log(userInfo + "|" + userComment);
    }

})();