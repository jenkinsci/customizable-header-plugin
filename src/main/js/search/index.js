import debounce from "lodash/debounce";
import { createElementFromHtml, xmlEscape } from "../util";

function init() {
  const rootUrl = document.head.dataset.rooturl;

  function classicSearch(q) {
    const searchUrl = document.querySelector("body").dataset.searchUrl;
    return fetch(`${searchUrl}?query=${encodeURIComponent(q)}`).then((rsp) => rsp.json().then((data) => {
      return data["suggestions"];
    }));
  }

  function adjustUrl(url) {
    if (url.startsWith("/")) {
      url = url.substring(1);
    }

    return rootUrl + "/" + url;
  }

  function renderClassicSearchResults() {
    const classicSearchInput = document.getElementById("ch-classic-search-input");
    const query = classicSearchInput.value;
    const resultsDiv = document.getElementById("ch-classic-search--results");
    if (query.length === 0) {
      resultsDiv.classList.toggle("jenkins-hidden", true);
      resultsDiv.innerHTML = "";
    } else {
      let results = Promise.all([classicSearch(query)]).then((e) => e.flat());
      results.then((results) => {
        resultsDiv.classList.toggle("jenkins-hidden", false);
        if (results.length > 0) {
          resultsDiv.innerHTML = "";
          let counter = 0;
          results.forEach((r) => {
            if (++counter <= 15) {
              const l = document.createElement("a");
              const url = adjustUrl(r["url"]);
              l.classList.add("jenkins-button", "jenkins-button--tertiary");
              let icon;
              if (r["type"] === "image") {
                icon = document.createElement("img");
                icon.src = r["icon"];
              } else {
                icon = createElementFromHtml(r["icon"]);
              }
              l.appendChild(icon);
              l.href = xmlEscape(url);
              l.appendChild(document.createTextNode(r["name"]));
              resultsDiv.appendChild(l);
            }
          })
        } else {
          resultsDiv.innerHTML = "<p>" +  resultsDiv.dataset.noResultsFor + " <code>" + xmlEscape(query) + "</code></p>";
        }
      });
    }

  }

  const debounceCS = debounce(() => {
    renderClassicSearchResults();
  }, 250);

  Behaviour.specify("#ch-classic-search-input", "ch-classic-search-input", 0, function(element) {
    const container = document.getElementById('ch-classic-search--results');
    element.addEventListener("input", () => {
      debounceCS();
    });
    element.addEventListener("focus", (e) => {
      if (e.target === element) {
        if (container.childElementCount > 0) {
          container.classList.remove('jenkins-hidden');
        }
      }
    });
    document.addEventListener("click", function(e) {
      if(e.target.id !== 'ch-classic-search--results' && e.target !== element) {
        //element clicked wasn't the div; hide the div
        container.classList.add('jenkins-hidden');
      }
    });
  });
}

export default { init };