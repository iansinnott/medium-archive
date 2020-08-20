(ns medium-archive.core
  (:gen-class)
  (:require [clojure.string :as s]
            [clojure.xml :as xml])
  (:import [org.jsoup Jsoup]))

(def html-test-str "
<!doctype html>
<html>
<head>
    <title>Example Domain</title>
    <meta charset=\"utf-8\" />
</head>

<body>
  <div>
    <h1>Example Domain</h1>
    <p>This domain is for use in illustrative examples in documents. You may use this
    domain in literature without prior coordination or asking for permission.</p>
    <p><a href=\"https://www.iana.org/domains/example\">More information...</a></p>
  </div>
</body>
</html>
")

(def example-doc (Jsoup/parse html-test-str))
(def medium-doc (Jsoup/parse (slurp "tmp-outfile.html")))
(-> example-doc
    (.select "p")
    (->> (map #(.text %))))

(-> example-doc
    (.select "body")
    (first)
    (.traverse (reify org.jsoup.select.NodeVisitor
                 (head [this node depth]
                   (println
                    (apply str
                           (s/join (repeat depth "  "))
                           "[ENTER]"
                           (.nodeName node))))
                 (tail [this node depth]
                   (println
                    (apply str
                           (s/join (repeat depth "  "))
                           "[EXIT]"
                           (.nodeName node)))))))

(def coll (.select example-doc "p"))
(map #(.text %) coll) ;; Works!

(comment
  (Jsoup/parse "<a href='nakkaya.com'/>")

  ;; This is expected to throw
  (xml/parse (java.io.ByteArrayInputStream. (.getBytes html-test-str))))


(defn save-url-to-file [url outfile]
  (let [contents (slurp url)]
    (spit outfile contents)))

(def test-url "https://medium.com/@ians/rendering-svgs-as-images-directly-in-react-a26615c45770")

(save-url-to-file test-url "tmp-outfile.html")

(save-url-to-file "http://www.example.com" "example.html")

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
