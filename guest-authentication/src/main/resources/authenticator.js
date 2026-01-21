let Auth = (() => {

  let baseUrl = "";
  let context = null;

  const constructUrl = (path) => {
    let url = baseUrl;
    if (url.lastIndexOf("/") === url.length) {
      url = url + path;
    } else {
      url = url + "./" + path;
    }
    return url;
  };

  const setContext = ({ data, url }) => {
    context = data;
    baseUrl = url;
  };

  const doLogin = (payload) => {
    return new Promise((resolve, reject) => {
      fetch(constructUrl("../authn/login"), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      })
        .then((response) => response.json())
        .then((data) => {
          if (data && data.accountId) {
            context = data;
            resolve(data);
          } else {
            reject(data);
          }
        })
        .catch((error) => {
          reject(`Chatbot login failed.`);
        });
    });
  };

  const doDecorateRequest = () => {
    let headers = {};
    if (context && context.clientSessionId) {
      headers = {
        "X-Krista-Context": encodeURIComponent(
          JSON.stringify({
            clientSessionId: context.clientSessionId,
          })
        ),
      };
    }
    return headers;
  };

  const doLogout = () => {
    return new Promise((resolve, reject) => {
      fetch(constructUrl("../authn/logout"), {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          ...doDecorateRequest(),
        },
        body: JSON.stringify({
          clientSessionId: context.clientSessionId,
        }),
      })
        .then((response) => response.json())
        .then((data) => {
          resolve(data);
        })
        .catch((error) => {
          reject(`Error in loading chatbot.`);
        });
    });
  };
  const getForm = () => {
    return "";
  };

  const formValues = (values) => {
    const payload = {};
    values.forEach((item) => {
      payload[item.name] = item.value;
    });
    return payload;
  };

  const validatePayload = (payload) => {
    let error = "";
    if (payload.email === "") {
      error = "Enter email";
    }
    return error;
  };

  return {
    init: setContext,
    login: doLogin,
    logout: doLogout,
    decorateRequest: doDecorateRequest,
    getFromFields: getForm,
    getPayload: formValues,
    validatePayload: validatePayload,
    constructUrl: constructUrl
  };

})();

window.Authenticator = Auth;
